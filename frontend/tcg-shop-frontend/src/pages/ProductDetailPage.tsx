import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { productApi, cartApi } from '@/lib/api'
import { getSessionId } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>()
  const [product, setProduct] = useState<any>(null)
  const [loading, setLoading] = useState(true)

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
    
    const sessionId = getSessionId()
    cartApi.addItem(sessionId, {
      productId: product.id,
      productName: product.name,
      quantity: 1,
      price: product.price,
    })
      .then(() => {
        alert('Added to cart!')
      })
      .catch(err => {
        console.error('Error adding to cart:', err)
      })
  }

  if (loading) {
    return <div>Loading...</div>
  }

  if (!product) {
    return <div>Product not found</div>
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{product.name}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="mb-4">{product.description}</p>
        <p className="text-3xl font-bold mb-4">${product.price}</p>
        <Button onClick={handleAddToCart}>Add to Cart</Button>
      </CardContent>
    </Card>
  )
}

