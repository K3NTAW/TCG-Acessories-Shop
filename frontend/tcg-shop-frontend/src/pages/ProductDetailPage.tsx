import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { productApi, cartApi } from '@/lib/api'
import { getSessionId } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'
import { ShoppingCart, ArrowLeft, Check } from 'lucide-react'
import { useToast } from '@/components/ui/use-toast'

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [product, setProduct] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [addingToCart, setAddingToCart] = useState(false)
  const { toast } = useToast()

  useEffect(() => {
    if (id) {
      productApi.getById(Number(id))
        .then(res => {
          setProduct(res.data)
          setLoading(false)
        })
        .catch(err => {
          console.error('Error fetching product:', err)
          setLoading(false)
        })
    }
  }, [id])

  const handleAddToCart = () => {
    if (!product) return
    
    setAddingToCart(true)
    const sessionId = getSessionId()
    cartApi.addItem(sessionId, {
      productId: product.id,
      productName: product.name,
      quantity: 1,
      price: product.price,
    })
      .then(() => {
        toast({
          title: "Added to cart",
          description: `${product.name} has been added to your cart.`,
        })
      })
      .catch(err => {
        console.error('Error adding to cart:', err)
        toast({
          title: "Error",
          description: "Failed to add item to cart. Please try again.",
          variant: "destructive",
        })
      })
      .finally(() => {
        setAddingToCart(false)
      })
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="grid gap-8 md:grid-cols-2">
          <Skeleton className="aspect-square w-full rounded-lg" />
          <div className="space-y-6">
            <Skeleton className="h-10 w-3/4" />
            <Skeleton className="h-6 w-1/2" />
            <Skeleton className="h-32 w-full" />
            <Skeleton className="h-12 w-1/3" />
            <Skeleton className="h-11 w-full" />
          </div>
        </div>
      </div>
    )
  }

  if (!product) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Card>
          <CardHeader>
            <CardTitle>Product not found</CardTitle>
          </CardHeader>
          <CardContent>
            <Button onClick={() => navigate('/products')}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back to Products
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  const formatCategory = (category: string) => {
    return category.split('_').map(word => 
      word.charAt(0) + word.slice(1).toLowerCase()
    ).join(' ')
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <Button 
        variant="ghost" 
        onClick={() => navigate('/products')}
        className="mb-6"
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Back to Products
      </Button>

      <div className="grid gap-8 md:grid-cols-2">
        {/* Product Image */}
        <div className="aspect-square overflow-hidden rounded-lg bg-muted">
          {product.imageUrl ? (
            <img 
              src={product.imageUrl} 
              alt={product.name}
              className="h-full w-full object-cover"
            />
          ) : (
            <div className="flex h-full w-full items-center justify-center">
              <ShoppingCart className="h-24 w-24 text-muted-foreground/30" />
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="space-y-6">
          <div>
            <Badge variant="secondary" className="mb-4">
              {formatCategory(product.category)}
            </Badge>
            <h1 className="text-4xl font-bold tracking-tight mb-4">
              {product.name}
            </h1>
            <p className="text-3xl font-semibold text-primary">
              ${product.price}
            </p>
          </div>

          <Separator />

          <div className="space-y-4">
            <div>
              <h3 className="text-lg font-semibold mb-2">Description</h3>
              <p className="text-muted-foreground leading-relaxed">
                {product.description}
              </p>
            </div>

            {product.stockQuantity !== undefined && (
              <div className="flex items-center gap-2">
                {product.stockQuantity > 0 ? (
                  <>
                    <Check className="h-5 w-5 text-green-600" />
                    <span className="text-sm text-muted-foreground">
                      In Stock ({product.stockQuantity} available)
                    </span>
                  </>
                ) : (
                  <Badge variant="destructive">Out of Stock</Badge>
                )}
              </div>
            )}
          </div>

          <Separator />

          <div className="flex gap-4">
            <Button 
              size="lg" 
              className="flex-1"
              onClick={handleAddToCart}
              disabled={addingToCart || (product.stockQuantity !== undefined && product.stockQuantity === 0)}
            >
              <ShoppingCart className="mr-2 h-5 w-5" />
              {addingToCart ? 'Adding...' : 'Add to Cart'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
