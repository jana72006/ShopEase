import { useEffect, useMemo, useState } from 'react'

const STORAGE_KEYS = {
  products: 'shopease-web-products-v1',
  cart: 'shopease-web-cart-v1',
  orders: 'shopease-web-orders-v1',
}
const TAX_RATE = 0.14
const CATEGORY_IMAGES = {
  Books: 'books.jpg',
  Clothing: 'clothes.jpg',
  Decor: 'decor.jpg',
  Electronics: 'elec.jpg',
}
const CATEGORIES = ['All finds', 'Electronics', 'Books', 'Clothing', 'Decor']
const imageBase = `${import.meta.env.BASE_URL}images/`

function readLocal(key, fallback) {
  try {
    const saved = localStorage.getItem(key)
    return saved ? JSON.parse(saved) : fallback
  } catch (error) {
    console.warn(`Could not read local ${key} data.`, error)
    return fallback
  }
}

function formatPrice(price) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(price)
}

function App() {
  const [seedProducts, setSeedProducts] = useState([])
  const [products, setProducts] = useState(() => readLocal(STORAGE_KEYS.products, null))
  const [cart, setCart] = useState(() => readLocal(STORAGE_KEYS.cart, []))
  const [orders, setOrders] = useState(() => readLocal(STORAGE_KEYS.orders, []))
  const [view, setView] = useState('shop')
  const [category, setCategory] = useState('All finds')
  const [search, setSearch] = useState('')
  const [sort, setSort] = useState('featured')
  const [notice, setNotice] = useState('')
  const [loadError, setLoadError] = useState('')
  const [editingProduct, setEditingProduct] = useState(null)

  useEffect(() => {
    let active = true
    fetch(`${import.meta.env.BASE_URL}data/products.json`)
      .then((response) => {
        if (!response.ok) throw new Error(`Catalog request failed (${response.status}).`)
        return response.json()
      })
      .then((catalog) => {
        if (!active) return
        setSeedProducts(catalog)
        setProducts((current) => current ?? catalog)
      })
      .catch((error) => {
        console.error('Could not load the ShopEase catalog.', error)
        if (active) setLoadError('We could not load the collection. Please refresh and try again.')
      })
    return () => {
      active = false
    }
  }, [])

  useEffect(() => {
    if (products) localStorage.setItem(STORAGE_KEYS.products, JSON.stringify(products))
  }, [products])
  useEffect(() => localStorage.setItem(STORAGE_KEYS.cart, JSON.stringify(cart)), [cart])
  useEffect(() => localStorage.setItem(STORAGE_KEYS.orders, JSON.stringify(orders)), [orders])

  useEffect(() => {
    if (!notice) return undefined
    const timer = window.setTimeout(() => setNotice(''), 2600)
    return () => window.clearTimeout(timer)
  }, [notice])

  const visibleProducts = useMemo(() => {
    const filtered = (products ?? []).filter((product) => {
      const matchesCategory = category === 'All finds' || product.category === category
      const matchesSearch = `${product.name} ${product.category}`.toLowerCase().includes(search.toLowerCase())
      return matchesCategory && matchesSearch
    })
    if (sort === 'price-low') return [...filtered].sort((a, b) => a.price - b.price)
    if (sort === 'price-high') return [...filtered].sort((a, b) => b.price - a.price)
    if (sort === 'name') return [...filtered].sort((a, b) => a.name.localeCompare(b.name))
    return filtered
  }, [category, products, search, sort])

  const cartCount = cart.reduce((total, item) => total + item.quantity, 0)
  const subtotal = cart.reduce((total, item) => total + item.price * item.quantity, 0)
  const tax = subtotal * TAX_RATE
  const total = subtotal + tax

  function addToCart(product) {
    const alreadyInCart = cart.find((item) => item.id === product.id)?.quantity ?? 0
    if (alreadyInCart >= product.stock) {
      setNotice('That is all the stock we have for this find.')
      return
    }
    setCart((items) => {
      const existing = items.find((item) => item.id === product.id)
      if (existing) return items.map((item) => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item)
      return [...items, { ...product, quantity: 1 }]
    })
    setNotice(`${product.name} added to your bag.`)
  }

  function changeQuantity(productId, delta) {
    const product = (products ?? []).find((item) => item.id === productId)
    if (!product) {
      setNotice('The catalog is still loading. Please try again in a moment.')
      return
    }
    setCart((items) => items.flatMap((item) => {
      if (item.id !== productId) return [item]
      const quantity = item.quantity + delta
      if (quantity <= 0) return []
      if (quantity > product.stock) {
        setNotice('That is all the stock we have for this find.')
        return [item]
      }
      return [{ ...item, quantity }]
    }))
  }

  function checkout() {
    if (!cart.length) return
    const order = {
      id: `BBM-${Date.now().toString().slice(-7)}`,
      placedAt: new Date().toISOString(),
      itemCount: cartCount,
      total,
    }
    const purchased = new Map(cart.map((item) => [item.id, item.quantity]))
    setProducts((items) => items.map((product) => ({
      ...product,
      stock: Math.max(0, product.stock - (purchased.get(product.id) ?? 0)),
    })))
    setOrders((items) => [order, ...items])
    setCart([])
    setView('shop')
    setNotice(`Demo order ${order.id} placed on this device. No payment was collected.`)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function saveProduct(product) {
    setProducts((items) => {
      if (editingProduct) return items.map((item) => item.id === product.id ? product : item)
      return [product, ...items]
    })
    setEditingProduct(null)
    setNotice(editingProduct ? 'Product updated in this browser.' : 'Product added in this browser.')
  }

  function deleteProduct(productId) {
    setProducts((items) => items.filter((product) => product.id !== productId))
    setCart((items) => items.filter((item) => item.id !== productId))
    setNotice('Product removed from this browser.')
  }

  function resetCatalog() {
    setProducts(seedProducts)
    setCart([])
    setNotice('Demo catalog restored in this browser.')
  }

  return (
    <>
      <div className="announcement">A little treat for your everyday rituals <span>✦</span> Thoughtfully picked, always</div>
      <header className="site-header">
        <button className="wordmark" onClick={() => setView('shop')} aria-label="Bye Bye Money home">
          <span className="wordmark-icon">b.</span>
          <span>BYE BYE MONEY<small>THE LITTLE JOY DEPARTMENT</small></span>
        </button>
        <nav className="main-nav" aria-label="Main navigation">
          <button className={view === 'shop' ? 'nav-link active' : 'nav-link'} onClick={() => setView('shop')}>Shop all</button>
          <button className="nav-link" onClick={() => { setView('shop'); document.getElementById('about')?.scrollIntoView({ behavior: 'smooth' }) }}>Our little story</button>
          <button className={view === 'admin' ? 'nav-link active' : 'nav-link'} onClick={() => setView('admin')}>Demo admin</button>
        </nav>
        <button className="bag-button" onClick={() => setView(view === 'cart' ? 'shop' : 'cart')} aria-label={`Open bag, ${cartCount} items`}>
          Bag <span className="bag-count">{cartCount}</span>
        </button>
      </header>

      <main>
        {view === 'shop' && (
          <>
            <section className="hero">
              <img className="hero-image" src={`${imageBase}main-bg.jpg`} alt="" />
              <div className="hero-shade" />
              <div className="hero-copy">
                <span className="eyebrow light">A softer kind of shopping</span>
                <h1>Small joys.<br /><em>Big main-character energy.</em></h1>
                <p>Little things that make an ordinary Tuesday feel a bit more like you.</p>
                <button className="button button-light" onClick={() => document.getElementById('collection')?.scrollIntoView({ behavior: 'smooth' })}>Find your little joy <span>↗</span></button>
              </div>
              <div className="hero-sticker"><span>GOOD<br />THINGS</span><b>♡</b></div>
              <div className="hero-caption">CURATED WITH A LITTLE EXTRA LOVE · EST. 2026</div>
            </section>

            <section className="collection section-wrap" id="collection">
              <div className="section-heading">
                <div><span className="eyebrow">YOUR NEXT LITTLE OBSESSION</span><h2>The feel-good edit<span className="lavender-dot">.</span></h2></div>
                <span className="collection-note">Cute, useful, and just a little bit unnecessary.</span>
              </div>
              <div className="category-row" aria-label="Filter by category">
                {CATEGORIES.map((item) => <button key={item} className={category === item ? 'category-chip selected' : 'category-chip'} onClick={() => setCategory(item)}>{item}</button>)}
              </div>
              <div className="toolbar">
                <label className="search-field"><span>⌕</span><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Find your next little joy..." aria-label="Search products" /><kbd>SEARCH</kbd></label>
                <label className="sort-field"><span>Sort by</span><select value={sort} onChange={(event) => setSort(event.target.value)} aria-label="Sort products"><option value="featured">Our favorites</option><option value="price-low">Price: low to high</option><option value="price-high">Price: high to low</option><option value="name">Name: A to Z</option></select></label>
              </div>
              {loadError && <p className="error-banner" role="alert">{loadError}</p>}
              <p className="results-count">{visibleProducts.length} LITTLE FINDS TO LOVE</p>
              <div className="product-grid">
                {visibleProducts.map((product, index) => <ProductCard key={product.id} product={product} index={index} onAdd={() => addToCart(product)} />)}
              </div>
              {!visibleProducts.length && !loadError && <div className="empty-state"><span>☁</span><h3>No little joys found.</h3><p>Try a different search or category.</p><button className="text-button" onClick={() => { setSearch(''); setCategory('All finds') }}>Clear filters ↗</button></div>}
            </section>

            <section className="story-band" id="about">
              <div className="story-image-wrap"><img src={`${imageBase}decor.jpg`} alt="A lavender candle and fresh flowers" /></div>
              <div className="story-copy"><span className="eyebrow">A NOTE FROM US</span><h2>Because “I deserve a little treat” is a whole lifestyle.</h2><p>We collect the cozy, the clever, and the completely delightful. The things that make your desk feel like yours, your evenings a little softer, and your everyday routines a little less everyday.</p><span className="signature">With love & questionable spending habits, <b>the BBM team ♡</b></span></div>
            </section>
          </>
        )}

        {view === 'cart' && <CartView cart={cart} subtotal={subtotal} tax={tax} total={total} onQuantity={changeQuantity} onContinue={() => setView('shop')} onCheckout={checkout} />}
        {view === 'admin' && <AdminView products={products ?? []} orders={orders} editingProduct={editingProduct} onEdit={setEditingProduct} onSave={saveProduct} onDelete={deleteProduct} onReset={resetCatalog} onCancel={() => setEditingProduct(null)} />}
      </main>

      <footer className="site-footer">
        <div className="footer-brand"><span className="wordmark-icon">b.</span><p>A little treat, a lot of joy.<br />© 2026 Bye Bye Money Store</p></div>
        <div className="footer-links"><a href="#collection" onClick={() => setView('shop')}>Shop</a><a href="#about" onClick={() => setView('shop')}>Our story</a><button onClick={() => setView('admin')}>Demo admin</button></div>
        <span className="footer-note">MADE FOR THE LITTLE MOMENTS ♡</span>
      </footer>
      <div className="demo-notice">WEB DEMO · Orders and admin edits stay in this browser. No real purchases or secure accounts.</div>
      {notice && <div className="toast" role="status">{notice}</div>}
    </>
  )
}

function ProductCard({ product, index, onAdd }) {
  const [imageFailed, setImageFailed] = useState(false)
  const image = !imageFailed && product.image ? product.image : `${imageBase}${CATEGORY_IMAGES[product.category] ?? 'decor.jpg'}`
  const lowStock = product.stock > 0 && product.stock < 5
  return (
    <article className="product-card" style={{ '--card-index': index % 8 }}>
      <div className="product-photo">
        <img src={image} alt={product.name} loading="lazy" onError={() => setImageFailed(true)} />
        {lowStock && <span className="product-badge">Almost gone</span>}
        <button className="quick-add" onClick={onAdd} disabled={product.stock < 1} aria-label={`Add ${product.name} to bag`}>{product.stock < 1 ? 'SOLD OUT' : 'ADD TO BAG +'}</button>
      </div>
      <div className="product-meta"><span>{product.category}</span><span>{formatPrice(product.price)}</span></div>
      <h3>{product.name}</h3>
      <p className="product-stock">{product.stock > 0 ? `${product.stock} little ${product.stock === 1 ? 'treasure' : 'treasures'} left` : 'Out of stock'}</p>
    </article>
  )
}

function CartView({ cart, subtotal, tax, total, onQuantity, onContinue, onCheckout }) {
  return (
    <section className="page-panel section-wrap">
      <button className="back-link" onClick={onContinue}>← Back to the good stuff</button>
      <div className="section-heading cart-heading"><div><span className="eyebrow">YOUR LITTLE PILE OF HAPPINESS</span><h2>The bag<span className="lavender-dot">.</span></h2></div><span className="collection-note">{cart.reduce((sum, item) => sum + item.quantity, 0)} lovely little things</span></div>
      {!cart.length ? <div className="empty-state"><span>♡</span><h3>Your bag is taking a little nap.</h3><p>Go find something that makes you smile.</p><button className="button button-dark" onClick={onContinue}>Explore the collection ↗</button></div> : (
        <div className="cart-layout">
          <div className="cart-items">{cart.map((item) => <article className="cart-item" key={item.id}><img src={item.image || `${imageBase}${CATEGORY_IMAGES[item.category] ?? 'decor.jpg'}`} alt="" onError={(event) => { event.currentTarget.src = `${imageBase}${CATEGORY_IMAGES[item.category] ?? 'decor.jpg'}` }} /><div className="cart-item-info"><span className="eyebrow">{item.category}</span><h3>{item.name}</h3><strong>{formatPrice(item.price)}</strong><div className="quantity-control"><button aria-label={`Remove one ${item.name}`} onClick={() => onQuantity(item.id, -1)}>−</button><span>{item.quantity}</span><button aria-label={`Add one ${item.name}`} onClick={() => onQuantity(item.id, 1)}>+</button></div></div><b className="line-total">{formatPrice(item.price * item.quantity)}</b></article>)}</div>
          <aside className="order-summary"><span className="eyebrow">THE NOT-SO-SERIOUS TOTAL</span><h3>Order summary</h3><div><span>Subtotal</span><span>{formatPrice(subtotal)}</span></div><div><span>Tax (14%)</span><span>{formatPrice(tax)}</span></div><div className="summary-total"><span>Total</span><b>{formatPrice(total)}</b></div><button className="button button-dark checkout-button" onClick={onCheckout}>Place demo order ♡</button><p>This is a demo only. No payment or delivery is involved.</p></aside>
        </div>
      )}
    </section>
  )
}

function AdminView({ products, orders, editingProduct, onEdit, onSave, onDelete, onReset, onCancel }) {
  const [formError, setFormError] = useState('')
  function submitProduct(event) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const name = String(form.get('name')).trim()
    const category = String(form.get('category'))
    const price = Number(form.get('price'))
    const stock = Number(form.get('stock'))
    const image = String(form.get('image')).trim()
    if (!name || !Number.isFinite(price) || price < 0 || !Number.isInteger(stock) || stock < 0) {
      setFormError('Add a name, a valid price, and a whole-number stock amount.')
      return
    }
    onSave({ id: editingProduct?.id ?? Math.max(0, ...products.map((product) => product.id)) + 1, name, category, price, stock, image })
    event.currentTarget.reset()
    setFormError('')
  }
  return (
    <section className="page-panel section-wrap admin-panel">
      <span className="eyebrow">A BROWSER-ONLY PLAYGROUND</span><div className="section-heading"><div><h2>Little shopkeeper<span className="lavender-dot">.</span></h2><p className="admin-lede">Changes live only in this browser. There is no shared admin login or online database.</p></div><button className="button button-outline" onClick={onReset}>Restore demo catalog</button></div>
      <div className="admin-stats"><div><span>PRODUCTS</span><b>{products.length}</b></div><div><span>DEMO ORDERS ON THIS DEVICE</span><b>{orders.length}</b></div><div><span>STORAGE</span><b>THIS BROWSER</b></div></div>
      <div className="admin-layout">
        <form className="admin-form" key={editingProduct?.id ?? 'new'} onSubmit={submitProduct}>
          <span className="eyebrow">{editingProduct ? 'MAKE IT JUST RIGHT' : 'ADD A NEW LITTLE FIND'}</span><h3>{editingProduct ? 'Edit product' : 'New product'}</h3>
          <label>Product name<input name="name" defaultValue={editingProduct?.name ?? ''} required maxLength="90" placeholder="Something lovely" /></label>
          <div className="form-two"><label>Category<select name="category" defaultValue={editingProduct?.category ?? 'Decor'}>{CATEGORIES.slice(1).map((item) => <option key={item}>{item}</option>)}</select></label><label>Price (USD)<input name="price" type="number" step="0.01" min="0" defaultValue={editingProduct?.price ?? ''} required /></label></div>
          <label>Stock on hand<input name="stock" type="number" step="1" min="0" defaultValue={editingProduct?.stock ?? 1} required /></label>
          <label>Image URL (optional)<input name="image" type="url" defaultValue={editingProduct?.image ?? ''} placeholder="https://..." /></label>
          {formError && <p className="form-error" role="alert">{formError}</p>}
          <div className="form-actions"><button className="button button-dark" type="submit">{editingProduct ? 'Save changes' : 'Add to the shop'} ↗</button>{editingProduct && <button className="text-button" type="button" onClick={onCancel}>Cancel</button>}</div>
        </form>
        <div className="inventory">
          <div className="inventory-heading"><div><span className="eyebrow">YOUR LITTLE LINEUP</span><h3>Inventory</h3></div><span>{products.length} items</span></div>
          {!products.length ? <p className="muted">No products yet. Add one to get started.</p> : products.map((product) => <div className="inventory-item" key={product.id}><img src={product.image || `${imageBase}${CATEGORY_IMAGES[product.category] ?? 'decor.jpg'}`} alt="" onError={(event) => { event.currentTarget.src = `${imageBase}${CATEGORY_IMAGES[product.category] ?? 'decor.jpg'}` }} /><div className="inventory-info"><b>{product.name}</b><span>{product.category} · {formatPrice(product.price)} · {product.stock} in stock</span></div><button className="icon-action" onClick={() => onEdit(product)} aria-label={`Edit ${product.name}`}>EDIT</button><button className="icon-action danger" onClick={() => { if (window.confirm(`Remove ${product.name} from this browser?`)) onDelete(product.id) }} aria-label={`Delete ${product.name}`}>✕</button></div>)}
        </div>
      </div>
      <div className="orders-panel"><span className="eyebrow">SAVED ON THIS DEVICE ONLY</span><h3>Demo orders</h3>{orders.length ? orders.map((order) => <div className="order-row" key={order.id}><b>{order.id}</b><span>{new Date(order.placedAt).toLocaleString()}</span><span>{order.itemCount} items</span><strong>{formatPrice(order.total)}</strong></div>) : <p className="muted">No demo orders yet. Order history stays in this browser.</p>}</div>
    </section>
  )
}

export default App
