# pbl-java

# Profit & Pricing Module with JDBC + AI Integration

## 📌 Overview
This module handles all financial calculations in the Dynamic Pricing System with integrated database connectivity and AI-powered price prediction.

It ensures that every suggested price is profitable, meets minimum margin requirements, and aligns with competitor pricing by:
1. **SEARCHING** products from database
2. **STORING** product and pricing data persistently
3. **PREDICTING** optimal prices using AI algorithms

---

## 🚀 Quick Start

### Run the AI Price Prediction Demo (No Database Required)
```bash
# Compile
cd /Users/aniketnegi/Downloads/pbl-java-main
javac -d build src/main/java/pricing/*.java src/main/java/IntegratedDemo.java

# Run interactive demo
java -cp build IntegratedDemo
```

---

## ✨ Features

### Core Pricing Features
- ✅ Profit calculation  
- ✅ Profit margin computation  
- ✅ Competitor price analysis  
- ✅ Smart price suggestion (demand + competition based)  
- ✅ Price validation (no loss, minimum margin enforcement)

### NEW - JDBC Integration
- ✅ Search products from database
- ✅ Store products with competitor prices
- ✅ Store pricing predictions and history
- ✅ Query pricing analytics
- ✅ Full CRUD operations for products and pricing data

### NEW - AI Price Prediction
- ✅ AI-powered price optimization
- ✅ Demand-based pricing strategy
- ✅ Competitor analysis
- ✅ Margin enforcement
- ✅ Profitability validation

---

## 📊 The Complete Workflow: SEARCH → STORE → PREDICT

### 1️⃣ SEARCH - Find Products from Database
```java
ProductDao productDao = new ProductDao();

// Search by name
List<ProductData> results = productDao.searchProducts("Laptop");

// Get all products
List<ProductData> allProducts = productDao.getAllProducts();
```

### 2️⃣ STORE - Save Product Data to Database
```java
// Store new product with competitor prices
int productId = productDao.insertProduct(
    "Laptop",           // name
    500.0,              // cost price
    "550,575,600"       // competitor prices
);
```

### 3️⃣ PREDICT - Use AI to Calculate Optimal Price
```java
// Predict optimal price using AI algorithm
double predictedPrice = PricePredictor.predictPrice(
    500.0,                              // cost price
    new double[]{550, 575, 600},       // competitor prices
    20.0,                               // minimum margin %
    0.75                                // demand level (0.0-1.0)
);

// Calculate profit and margins
double profit = PricePredictor.calculateProfit(500.0, predictedPrice);
double margin = PricePredictor.calculateMarginPercent(500.0, predictedPrice);

// Validate profitability
boolean isProfitable = PricePredictor.isProfitable(500.0, predictedPrice);
boolean meetMargin = PricePredictor.meetsMargin(500.0, predictedPrice, 20.0);

// Store prediction in database
pricingDao.insertPricePrediction(
    productId,
    predictedPrice,
    profit,
    margin,
    isProfitable,
    meetMargin,
    "AI Predicted - Demand: 75%, Min Margin: 20%"
);
```

---

## 🗄️ Database Setup (MySQL)

### Prerequisites
```bash
# Install MySQL
brew install mysql              # macOS
sudo apt-get install mysql-server  # Linux

# Start MySQL
brew services start mysql       # macOS
sudo systemctl start mysql      # Linux
```

### Create Database & Tables
```sql
-- 1. Create database
CREATE DATABASE pbl_database;
USE pbl_database;

-- 2. Products table
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    cost_price DECIMAL(10, 2) NOT NULL,
    competitor_prices VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Pricing history table
CREATE TABLE pricing_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    predicted_price DECIMAL(10, 2) NOT NULL,
    profit DECIMAL(10, 2) NOT NULL,
    margin_percent DECIMAL(5, 2) NOT NULL,
    is_profitable BOOLEAN NOT NULL,
    meets_margin BOOLEAN NOT NULL,
    prediction_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 4. Users table (optional, for user management)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Configure Database Connection
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/pbl_database
db.user=root
db.password=your_mysql_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Download JDBC Driver
```bash
# Download MySQL JDBC driver
cd lib/
curl -L -o mysql-connector-java-8.0.33.jar \
  "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
```

---

## 💻 Running with MySQL Database

### Compile All Sources
```bash
javac -d build -cp "lib/*" \
  src/main/java/db/*.java \
  src/main/java/pricing/*.java \
  src/main/java/IntegratedPricingApp.java
```

### Run the Full Application
```bash
java -cp "build:lib/*" IntegratedPricingApp
```

### Interactive Workflow
```
1. 🔍 SEARCH - Find products in database
   → Enter product name to search
   
2. ➕ STORE - Add new product
   → Enter product name, cost price, competitor prices
   
3. 🤖 PREDICT - AI price prediction
   → Select product
   → Enter minimum margin %
   → Enter demand level (0.0-1.0)
   → View AI prediction
   → Save to database
   
4. 📊 VIEW - Show all predictions
   → View complete pricing history
   
5. ❌ EXIT
```

---

## 📁 Project Structure

```
pbl-java-main/
├── pom.xml                           # Maven configuration
├── README.md                         # This file
│
├── lib/                              # JDBC Drivers
│   └── mysql-connector-java-8.0.33.jar
│
├── build/                            # Compiled classes
│   ├── db/
│   │   ├── DatabaseUtil.class
│   │   ├── UserDao.class
│   │   ├── ProductDao.class
│   │   └── PricingHistoryDao.class
│   ├── pricing/
│   │   └── PricePredictor.class
│   └── IntegratedPricingApp.class
│
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── IntegratedDemo.java          # Demo (no database needed)
    │   │   ├── IntegratedPricingApp.java    # Full app (with database)
    │   │   │
    │   │   ├── db/                          # Database Access Objects
    │   │   │   ├── DatabaseUtil.java        # Connection management
    │   │   │   ├── UserDao.java             # User operations
    │   │   │   ├── ProductDao.java          # Product CRUD
    │   │   │   └── PricingHistoryDao.java   # Pricing history CRUD
    │   │   │
    │   │   └── pricing/                     # AI Pricing Engine
    │   │       └── PricePredictor.java      # ML-based pricing
    │   │
    │   └── resources/
    │       └── db.properties                # Database config
    │
    └── old/  (Original pricing modules)
        ├── Product.java
        ├── ProfitCalculator.java
        ├── PriceValidator.java
        ├── SmartPricing.java
        └── MainTest.java
```

---

## 🧮 AI Price Prediction Algorithm

### Smart Pricing Strategy
```
1. Calculate minimum safe price:
   minPrice = costPrice × (1 + marginPercent/100)

2. Analyze competitor pricing:
   avgCompetitor = average of competitor prices
   competitivePrice = avgCompetitor - 2 (slight undercut)

3. Apply demand elasticity:
   IF demandLevel > 0.7:
       → HIGH DEMAND: Add $5 premium
   ELSE IF demandLevel < 0.3:
       → LOW DEMAND: Reduce $5
   ELSE:
       → MEDIUM DEMAND: Match competitive price

4. Ensure profitability:
   finalPrice = MAX(demandAdjustedPrice, minPrice)
```

### Example
```
Cost Price:           $500
Competitor Prices:    $550, $575, $600
Average Competitor:   $575
Minimum Margin:       20%
Demand Level:         75% (High)

Calculation:
- minPrice = 500 × (1 + 20/100) = $600
- competitivePrice = 575 - 2 = $573
- With 75% demand → Add $5 = $578
- finalPrice = MAX(578, 600) = $600

Result: PREDICTED PRICE = $600
```

---

## 🔑 Key Classes

### DatabaseUtil
- Manages database connections
- Loads configuration from `db.properties`
- Tests connection availability

### ProductDao
- **insertProduct()** - Add new product
- **searchProducts()** - Search by name
- **getAllProducts()** - Retrieve all
- **getProductById()** - Get specific product
- **updateProduct()** - Update details
- **deleteProduct()** - Remove product

### PricingHistoryDao
- **insertPricePrediction()** - Save AI prediction
- **getPricingHistory()** - Get product history
- **getAllPricingHistory()** - Get all predictions
- **getLatestPrediction()** - Get most recent

### PricePredictor
- **predictPrice()** - Generate optimal price
- **predictAdvancedPrice()** - ML-advanced pricing
- **calculateProfit()** - Profit calculation
- **calculateMarginPercent()** - Margin computation
- **isProfitable()** - Profitability check
- **meetsMargin()** - Margin validation

---

## 🧪 Example Usage

### Demo (No Database)
```bash
java -cp build IntegratedDemo
```

### Full System (With Database)
```bash
java -cp "build:lib/*" IntegratedPricingApp
```

### Command Line Example
```java
ProductDao dao = new ProductDao();
dao.createTable();

// Store product
int id = dao.insertProduct("Laptop", 500, "550,575,600");

// Search product
List<ProductData> products = dao.searchProducts("Laptop");

// Predict price
double predictedPrice = PricePredictor.predictPrice(
    500,
    new double[]{550, 575, 600},
    20,  // 20% margin
    0.75 // 75% demand
);
// Result: $600

// Save prediction
pricingDao.insertPricePrediction(id, predictedPrice, 100, 20, true, true, "AI prediction");
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| MySQL connection failed | Ensure MySQL is running: `brew services start mysql` |
| Driver not found | Download JDBC driver to `lib/` folder |
| Database not found | Create database: `CREATE DATABASE pbl_database;` |
| Class not found | Recompile: `javac -d build src/main/java/**/*.java` |
| Port 3306 in use | Change MySQL port in `db.properties` |

---

## 📚 Next Steps

1. ✅ Set up MySQL database
2. ✅ Configure `db.properties`
3. ✅ Download JDBC driver
4. ✅ Run `IntegratedPricingApp`
5. ⏭️ Add REST API for web integration
6. ⏭️ Implement advanced ML pricing
7. ⏭️ Add real-time market data integration
8. ⏭️ Build analytics dashboard

---

## 📝 License
All rights reserved © 2026
