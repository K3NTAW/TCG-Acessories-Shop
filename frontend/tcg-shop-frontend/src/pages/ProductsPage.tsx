import { useEffect, useState } from 'react'
import { productApi } from '@/lib/api'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Link } from 'react-router-dom'
import { ShoppingCart, ArrowRight, Box } from 'lucide-react'

interface Product {
  id: number
  name: string
  description: string
  price: number
  category: string
  imageUrl?: string
  stockQuantity?: number
}

function ProductCardSkeleton() {
  return (
    <Card className="overflow-hidden">
      <Skeleton className="h-64 w-full" />
      <CardHeader>
        <Skeleton className="h-6 w-3/4 mb-2" />
        <Skeleton className="h-4 w-1/2" />
      </CardHeader>
      <CardContent>
        <Skeleton className="h-4 w-full mb-2" />
        <Skeleton className="h-4 w-5/6" />
      </CardContent>
      <CardFooter>
        <Skeleton className="h-10 w-full" />
      </CardFooter>
    </Card>
  )
}

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    productApi.getAll()
      .then(res => {
        setProducts(res.data)
        setLoading(false)
      })
      .catch(err => {
        console.error('Error fetching products:', err)
        setLoading(false)
      })
  }, [])

  const formatCategory = (category: string) => {
    return category.split('_').map(word => 
      word.charAt(0) + word.slice(1).toLowerCase()
    ).join(' ')
  }

  return (
    <div className="container mx-auto px-4 py-12 md:py-16">
      <div className="mb-12 space-y-4">
        <h1 className="text-4xl font-bold tracking-tight">Products</h1>
        <p className="text-lg text-muted-foreground max-w-2xl">
          Discover our collection of premium 3D-printed TCG accessories
        </p>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {[...Array(8)].map((_, i) => (
            <ProductCardSkeleton key={i} />
          ))}
        </div>
      ) : products.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <p className="text-lg text-muted-foreground mb-4">No products found</p>
          <Button asChild>
            <Link to="/">Go Home</Link>
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {products.map((product) => (
            <Card 
              key={product.id} 
              className="group overflow-hidden transition-all hover:shadow-lg hover:border-primary/50"
            >
              <div className="relative aspect-square overflow-hidden bg-muted">
                {product.imageUrl ? (
                  <img 
                    src={product.imageUrl} 
                    alt={product.name}
                    className="h-full w-full object-cover transition-transform group-hover:scale-105"
                  />
                ) : (
                  <div className="flex h-full w-full items-center justify-center">
                    <Box className="h-16 w-16 text-muted-foreground/50" />
                  </div>
                )}
                <Badge 
                  className="absolute top-3 right-3"
                  variant="secondary"
                >
                  {formatCategory(product.category)}
                </Badge>
              </div>
              
              <CardHeader>
                <CardTitle className="line-clamp-1">{product.name}</CardTitle>
                <CardDescription className="line-clamp-2">
                  {product.description}
                </CardDescription>
              </CardHeader>
              
              <CardContent className="space-y-4">
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-bold">${product.price}</span>
                  {product.stockQuantity !== undefined && (
                    <Badge variant={product.stockQuantity > 0 ? "default" : "destructive"}>
                      {product.stockQuantity > 0 ? 'In Stock' : 'Out of Stock'}
                    </Badge>
                  )}
                </div>
              </CardContent>
              
              <CardFooter>
                <Button asChild className="w-full group">
                  <Link to={`/products/${product.id}`}>
                    View Details
                    <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                  </Link>
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
