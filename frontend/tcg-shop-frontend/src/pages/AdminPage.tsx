import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Textarea } from '@/components/ui/textarea'
import { useToast } from '@/components/ui/use-toast'
import { productApi } from '@/lib/api'
import { ArrowLeft, Save } from 'lucide-react'

type ProductCategory = 'DECK_BOX' | 'CARD_HOLDER' | 'STORAGE_SOLUTION' | 'PLAYMAT_ACCESSORY' | 'CUSTOM_DESIGN'

export default function AdminPage() {
  const navigate = useNavigate()
  const { toast } = useToast()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: '' as ProductCategory | '',
    price: '',
    stockQuantity: '',
    imageUrl: '',
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.category) {
      toast({
        title: "Validation Error",
        description: "Please select a category",
        variant: "destructive",
      })
      return
    }

    setLoading(true)
    
    try {
      const response = await productApi.create({
        name: formData.name,
        description: formData.description,
        category: formData.category,
        price: parseFloat(formData.price),
        stockQuantity: parseInt(formData.stockQuantity),
        imageUrl: formData.imageUrl || undefined,
      })

      toast({
        title: "Success!",
        description: `Product "${response.data.name}" has been created.`,
      })

      // Reset form
      setFormData({
        name: '',
        description: '',
        category: '' as ProductCategory | '',
        price: '',
        stockQuantity: '',
        imageUrl: '',
      })

      // Optionally navigate to products page
      setTimeout(() => {
        navigate('/products')
      }, 1500)
    } catch (error: any) {
      console.error('Error creating product:', error)
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to create product. Please try again.",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="mb-8">
        <Button 
          variant="ghost" 
          onClick={() => navigate('/products')}
          className="mb-4"
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Products
        </Button>
        <h1 className="text-4xl font-bold tracking-tight mb-2">Add New Product</h1>
        <p className="text-muted-foreground">
          Create a new product for your TCG accessories shop
        </p>
      </div>

      <Card className="max-w-2xl">
        <CardHeader>
          <CardTitle>Product Information</CardTitle>
          <CardDescription>
            Fill in the details below to add a new product to your catalog
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="name">Product Name *</Label>
              <Input
                id="name"
                placeholder="e.g., Premium Deck Box Pro"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
                disabled={loading}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                placeholder="Describe your product..."
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={4}
                disabled={loading}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="category">Category *</Label>
                <Select
                  value={formData.category}
                  onValueChange={(value) => setFormData({ ...formData, category: value as ProductCategory })}
                  disabled={loading}
                  required
                >
                  <SelectTrigger id="category">
                    <SelectValue placeholder="Select category" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="DECK_BOX">Deck Box</SelectItem>
                    <SelectItem value="CARD_HOLDER">Card Holder</SelectItem>
                    <SelectItem value="STORAGE_SOLUTION">Storage Solution</SelectItem>
                    <SelectItem value="PLAYMAT_ACCESSORY">Playmat Accessory</SelectItem>
                    <SelectItem value="CUSTOM_DESIGN">Custom Design</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="stockQuantity">Stock Quantity *</Label>
                <Input
                  id="stockQuantity"
                  type="number"
                  min="0"
                  placeholder="0"
                  value={formData.stockQuantity}
                  onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                  required
                  disabled={loading}
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="price">Price ($) *</Label>
                <Input
                  id="price"
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="29.99"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  required
                  disabled={loading}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="imageUrl">Image URL</Label>
                <Input
                  id="imageUrl"
                  type="url"
                  placeholder="https://example.com/image.jpg"
                  value={formData.imageUrl}
                  onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                  disabled={loading}
                />
              </div>
            </div>

            <div className="flex gap-4 pt-4">
              <Button 
                type="submit" 
                size="lg" 
                className="flex-1"
                disabled={loading}
              >
                {loading ? (
                  <>Creating...</>
                ) : (
                  <>
                    <Save className="mr-2 h-4 w-4" />
                    Create Product
                  </>
                )}
              </Button>
              <Button 
                type="button" 
                variant="outline" 
                size="lg"
                onClick={() => navigate('/products')}
                disabled={loading}
              >
                Cancel
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

