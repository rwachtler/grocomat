-- ************************************************************************************************
-- Cleaning up
-- ************************************************************************************************
DROP ALL OBJECTS; 


-- ************************************************************************************************
-- Creating tables and relationships
-- ************************************************************************************************

-- Stores
CREATE TABLE STORES(
	PK_NAME VARCHAR(50) PRIMARY KEY,
);

-- Items
CREATE TABLE ITEMS(
	PK_EAN BIGINT PRIMARY KEY, 
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(255),
	PRICE REAL,
	FK_STORE_NAME VARCHAR(50),
	FOREIGN KEY(FK_STORE_NAME) REFERENCES STORES(PK_NAME)
);

-- Categories
CREATE TABLE CATEGORIES(
	PK_ID INT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50),
	FK_ITEM_EAN BIGINT,
	FOREIGN KEY(FK_ITEM_EAN) REFERENCES ITEMS(PK_EAN)
);




