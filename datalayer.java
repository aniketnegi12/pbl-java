package com.pricing.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ============================================================
 *  DatabaseManager.java
 *  Member 3 – Data Layer & Storage Developer
 *  Role: Database connection lifecycle, schema creation, utilities
 * ============================================================
 *
 *  Responsibilities covered:
 *   - Singleton connection management (SQLite via JDBC)
 *   - Auto-create tables on first run
 *   - Utility methods: beginTransaction, commit, rollback, close
 *   - Logging all DB events for audit trail
 *
 *  Database: SQLite (file-based, zero-config, portable)
 *  File:     pricing_system.db  (auto-created in project root)
 * ============================================================
 */
public class DatabaseManager {

    /* ── Constants ─────────────────────────────────────── */
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL  = "jdbc:sqlite:pricing_system.db";
    private static final String DRIVER  = "org.sqlite.JDBC";

    /* ── Singleton State ────────────────────────────────── */
    private static DatabaseManager instance;
    private Connection connection;

    /* ─────────────────────────────────────────────────────
       Constructor – private (Singleton pattern)
       Loads driver, opens connection, initialises schema
    ───────────────────────────────────────────────────── */
    private DatabaseManager() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);          // default: auto-commit ON
            LOGGER.info("Database connected: " + DB_URL);
            initializeSchema();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQLite JDBC driver not found. Add sqlite-jdbc.jar to classpath.", e);
            throw new RuntimeException("DB driver missing", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Cannot open database connection.", e);
            throw new RuntimeException("DB connection failed", e);
        }
    }

    /* ─────────────────────────────────────────────────────
       getInstance() – Thread-safe lazy initialisation
    ───────────────────────────────────────────────────── */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null || !instance.isConnected()) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /* ─────────────────────────────────────────────────────
       getConnection() – Returns active JDBC Connection
    ───────────────────────────────────────────────────── */
    public Connection getConnection() {
        return connection;
    }

    /* ─────────────────────────────────────────────────────
       isConnected() – Health check
    ───────────────────────────────────────────────────── */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /* ─────────────────────────────────────────────────────
       Transaction Helpers
    ───────────────────────────────────────────────────── */
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
        LOGGER.fine("Transaction started.");
    }

    public void commit() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
        LOGGER.fine("Transaction committed.");
    }

    public void rollback() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
                LOGGER.warning("Transaction rolled back.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Rollback failed.", e);
        }
    }

    /* ─────────────────────────────────────────────────────
       closeConnection() – Call at application shutdown
    ───────────────────────────────────────────────────── */
    public void closeConnection() {
        try {
            if (isConnected()) {
                connection.close();
                LOGGER.info("Database connection closed.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error while closing connection.", e);
        }
    }

    /* ─────────────────────────────────────────────────────
       initializeSchema()
       Creates all tables if they do not already exist.
       Called once at startup – safe to run multiple times.
    ───────────────────────────────────────────────────── */
    private void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // ── Enable WAL mode for better concurrent reads ──
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA foreign_keys = ON;");

            // ── Table 1: products ────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    product_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_name     TEXT    NOT NULL,
                    category         TEXT    NOT NULL DEFAULT 'General',
                    cost_price       REAL    NOT NULL CHECK(cost_price >= 0),
                    competitor_price REAL             CHECK(competitor_price >= 0),
                    suggested_price  REAL,
                    profit_margin    REAL,
                    created_at       TEXT    NOT NULL DEFAULT (datetime('now')),
                    updated_at       TEXT    NOT NULL DEFAULT (datetime('now'))
                );
            """);

            // ── Table 2: pricing_history ─────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pricing_history (
                    history_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id       INTEGER NOT NULL,
                    old_price        REAL,
                    new_price        REAL    NOT NULL,
                    cost_price       REAL    NOT NULL,
                    profit_margin    REAL,
                    strategy_used    TEXT    DEFAULT 'AI',
                    changed_at       TEXT    NOT NULL DEFAULT (datetime('now')),
                    notes            TEXT,
                    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
                );
            """);

            // ── Table 3: competitor_prices ───────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS competitor_prices (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id       INTEGER NOT NULL,
                    competitor_name  TEXT    NOT NULL,
                    price            REAL    NOT NULL CHECK(price >= 0),
                    recorded_at      TEXT    NOT NULL DEFAULT (datetime('now')),
                    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
                );
            """);

            // ── Indexes for fast lookups ─────────────────────
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_name     ON products(product_name);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_history_product   ON pricing_history(product_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_competitor_product ON competitor_prices(product_id);");

            LOGGER.info(" Schema initialised (tables: products, pricing_history, competitor_prices).");
        }
    }

    /* ─────────────────────────────────────────────────────
       executeUpdate() – Generic DML helper (INSERT/UPDATE/DELETE)
       Returns rows affected.
    ───────────────────────────────────────────────────── */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            int rows = ps.executeUpdate();
            LOGGER.fine("executeUpdate affected " + rows + " row(s). SQL: " + sql);
            return rows;
        }
    }

    /* ─────────────────────────────────────────────────────
       executeQuery() – Generic SELECT helper
       Caller is responsible for closing the ResultSet.
    ───────────────────────────────────────────────────── */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        bindParams(ps, params);
        return ps.executeQuery();
    }

    /* ─────────────────────────────────────────────────────
       bindParams() – Internal helper to bind ? parameters
    ───────────────────────────────────────────────────── */
    private void bindParams(PreparedStatement ps, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}
