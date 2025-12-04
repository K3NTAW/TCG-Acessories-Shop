import { useEffect, useState } from 'react'
import { cartApi } from '@/lib/api'
import { getSessionId } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

export default function CartPage() {
  const [cart, setCart] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const sessionId = getSessionId()
    cartApi.get(sessionId)
      .then(res => {
        setCart(res.data)
        setLoading(false)
      })
      .catch(err => {
        console.error('Error fetching cart:', err)
        setLoading(false)
      })
  }, [])

  if (loading) {
    return <div>Loading cart...</div>
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return <div>Your cart is empty</div>
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>
      <Card>
        <CardHeader>
          <CardTitle>Cart Items</CardTitle>
        </CardHeader>
        <CardContent>
          {cart.items.map((item: any) => (
            <div key={item.productId} className="flex justify-between items-center mb-4">
              <div>
                <p className="font-bold">{item.productName}</p>
                <p>Quantity: {item.quantity}</p>
                <p>Price: ${item.price}</p>
              </div>
              <Button
                variant="destructive"
                onClick={() => {
                  const sessionId = getSessionId()
                  cartApi.removeItem(sessionId, item.productId)
                    .then(() => window.location.reload())
                }}
              >
                Remove
              </Button>
            </div>
          ))}
          <div className="mt-4 pt-4 border-t">
            <p className="text-2xl font-bold">Total: ${cart.total}</p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

