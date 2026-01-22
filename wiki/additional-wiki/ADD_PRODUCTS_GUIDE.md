# 📦 How to Add Products

## Method 1: Using the Admin Page (Recommended)

1. **Start the application** (if not already running):
   ```bash
   # Start backend services (see START_GUIDE.md)
   # Start frontend
   cd frontend/tcg-shop-frontend
   npm run dev
   ```

2. **Navigate to Admin Page**:
   - Open your browser to `http://localhost:3000`
   - Click the **"Admin"** button in the navbar (or go directly to `http://localhost:3000/admin`)

3. **Fill in the Product Form**:
   - **Product Name** (required): e.g., "Premium Deck Box Pro"
   - **Description**: Detailed description of the product
   - **Category** (required): Select from:
     - Deck Box
     - Card Holder
     - Storage Solution
     - Playmat Accessory
     - Custom Design
   - **Stock Quantity** (required): Number of items in stock
   - **Price** (required): Price in USD (e.g., 29.99)
   - **Image URL** (optional): URL to product image

4. **Click "Create Product"**
   - You'll see a success toast notification
   - The product will be added to your catalog
   - You'll be redirected to the products page

## Method 2: Using the API Directly

You can also add products using the REST API:

### Using cURL:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Deck Box Pro",
    "description": "A premium 3D-printed deck box with magnetic closure",
    "category": "DECK_BOX",
    "price": 29.99,
    "stockQuantity": 50,
    "imageUrl": "https://example.com/deck-box.jpg"
  }'
```

### Using Postman:

1. Create a new POST request to `http://localhost:8080/api/products`
2. Set Headers: `Content-Type: application/json`
3. Body (raw JSON):
```json
{
  "name": "Premium Deck Box Pro",
  "description": "A premium 3D-printed deck box with magnetic closure",
  "category": "DECK_BOX",
  "price": 29.99,
  "stockQuantity": 50,
  "imageUrl": "https://example.com/deck-box.jpg"
}
```

## Available Categories

- `DECK_BOX` - Deck Boxes
- `CARD_HOLDER` - Card Holders
- `STORAGE_SOLUTION` - Storage Solutions
- `PLAYMAT_ACCESSORY` - Playmat Accessories
- `CUSTOM_DESIGN` - Custom Designs

## Example Products

Here are some example products you can add:

### Example 1: Deck Box
```json
{
  "name": "Elite Deck Box",
  "description": "Premium 3D-printed deck box with secure magnetic closure and card capacity for 100+ cards.",
  "category": "DECK_BOX",
  "price": 24.99,
  "stockQuantity": 30,
  "imageUrl": ""
}
```

### Example 2: Card Holder
```json
{
  "name": "Display Card Holder",
  "description": "Elegant acrylic card holder perfect for displaying your favorite cards.",
  "category": "CARD_HOLDER",
  "price": 12.99,
  "stockQuantity": 50,
  "imageUrl": ""
}
```

### Example 3: Storage Solution
```json
{
  "name": "Collection Organizer",
  "description": "Modular storage system for organizing large card collections with customizable compartments.",
  "category": "STORAGE_SOLUTION",
  "price": 49.99,
  "stockQuantity": 20,
  "imageUrl": ""
}
```

## Notes

- **Price**: Must be greater than 0
- **Stock Quantity**: Must be a non-negative integer
- **Image URL**: Optional, but recommended for better product presentation
- **Category**: Must match one of the enum values exactly
- Products are immediately available after creation

## Troubleshooting

- **"Failed to create product"**: Make sure the Product Catalog Service is running on port 8081
- **"Category is required"**: Select a category from the dropdown
- **"Price must be greater than 0"**: Enter a valid price (e.g., 29.99)
- **Network errors**: Check that the API Gateway (port 8080) and Product Catalog Service are running

