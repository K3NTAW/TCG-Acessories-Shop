import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

export default function HomePage() {
  return (
    <div className="space-y-8">
      <section className="text-center space-y-4">
        <h1 className="text-4xl font-bold">3D Printed TCG Accessories</h1>
        <p className="text-xl text-muted-foreground">
          Premium accessories for your Trading Card Game collection
        </p>
        <Link to="/products">
          <Button size="lg">Shop Now</Button>
        </Link>
      </section>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Deck Boxes</CardTitle>
            <CardDescription>Protect your decks in style</CardDescription>
          </CardHeader>
          <CardContent>
            <Link to="/products?category=DECK_BOX">
              <Button variant="outline">Browse</Button>
            </Link>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Card Holders</CardTitle>
            <CardDescription>Display your favorite cards</CardDescription>
          </CardHeader>
          <CardContent>
            <Link to="/products?category=CARD_HOLDER">
              <Button variant="outline">Browse</Button>
            </Link>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Storage Solutions</CardTitle>
            <CardDescription>Organize your collection</CardDescription>
          </CardHeader>
          <CardContent>
            <Link to="/products?category=STORAGE_SOLUTION">
              <Button variant="outline">Browse</Button>
            </Link>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}

