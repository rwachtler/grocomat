Grocomat
====================

Assumptions
---------------------


1. Shopping List generator with price optimization*
2. *Each Item has a Name and an EAN Code*
- The Name may be the same
- The EAN Code is unique
- One Item can have 0:n categories
3. *Stores are selling Items*
- *Each Store has a unique name*
- *The prices for the same Item may vary between the stores*
- *Not every store has the same Items*

Shopping list
---------------------

1. A shopping list may contain
* The EAN Code
* The Name of the Product
* Product category
2. It can also contain Items that are not available in any
store.—> Mark them at the output.
3. The shopping list class should compute the cheapest
possible way to buy all things on it.
4. A shopping list considers just a few stores.
5. Example
* Shoppinglist
    * Mars
    * Bread
	  * Meat
	  * 08154711007

Shopping list logic
---------------------

1. Always priorize: EAN, Name, Category
2. If EAN:
* Search in selected stores for the product and pick the
cheapest one.
3. If Name:
* Find stores that are selling products that have this name.
* Pick the cheapest store.
4. If Category:
* Find all Products with this category
* Search in all stores for all found products
* Pick the cheapest one

Functional requirements
---------------------

1. Implement persistence
* Stores, Prices, Items and Categories have to be persistable in a Database
(via Hibernate, JDBC or OrientDB) → ShoppingTester.testPersistence()
* Everything should run from an empty database (so the startup script or
schema-generation-scripts have to be placed somewhere within your code)
2. Extend the ShoppingList class
* Add unimplemented methods (and Logic)


