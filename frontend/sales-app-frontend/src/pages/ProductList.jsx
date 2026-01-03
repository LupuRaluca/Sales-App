import { useEffect, useState, useMemo } from 'react';
import { ProductService } from '../api/productService';
import ProductCard from '../components/ProductCard';
import './Products.css';
import {useCart} from "../context/CartContext.jsx";

export default function ProductList() {
    const [products, setProducts] = useState([]); // Toate produsele (RAW data)
    const [filteredProducts, setFilteredProducts] = useState([]); // Produsele afișate
    const { addToCart } = useCart();

    // Stări pentru filtre
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedBrand, setSelectedBrand] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [sortOrder, setSortOrder] = useState('newest'); // default: noutăți

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadProducts();
    }, []);

    const uniqueBrands = useMemo(() => {
        return [...new Set(products.map(p => p.brandName))].sort();
    }, [products]);

    const uniqueCategories = useMemo(() => {
        return [...new Set(products.map(p => p.categoryName))].sort();
    }, [products]);

    useEffect(() => {
        let result = [...products];

        // A. Filtrare Text (Search)
        if (searchTerm) {
            const lowerTerm = searchTerm.toLowerCase();
            result = result.filter(p =>
                p.name.toLowerCase().includes(lowerTerm) ||
                p.description.toLowerCase().includes(lowerTerm)
            );
        }

        // B. Filtrare Brand
        if (selectedBrand) {
            result = result.filter(p => p.brandName === selectedBrand);
        }

        // C. Filtrare Categorie
        if (selectedCategory) {
            result = result.filter(p => p.categoryName === selectedCategory);
        }

        // D. Sortare
        switch (sortOrder) {
            case 'price-asc':
                result.sort((a, b) => a.price - b.price);
                break;
            case 'price-desc':
                result.sort((a, b) => b.price - a.price);
                break;
            case 'newest':
            default:
                // Presupunem că ID mai mare = produs mai nou
                result.sort((a, b) => b.id - a.id);
                break;
        }

        setFilteredProducts(result);

    }, [products, searchTerm, selectedBrand, selectedCategory, sortOrder]);

    const loadProducts = async () => {
        try {
            const response = await ProductService.getAllProducts();
            setProducts(response.data);
            // Nu setăm filteredProducts aici, se va ocupa useEffect-ul de mai sus
        } catch (err) {
            console.error("Eroare backend:", err);
            setError('Nu s-au putut încărca produsele.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async (product) => {
        // Apelăm funcția din context
        const success = await addToCart(product.id, 1);
        if (success) {
        }
    };

    if (loading) return <div className="loading-spinner">Se încarcă catalogul...</div>;
    if (error) return <div className="error-msg">{error}</div>;

    return (
        <div className="catalog-container">
            <div className="catalog-header">
                <div className="header-top">
                    <h1 className="catalog-title">Componente Hardware</h1>
                    <input
                        type="text"
                        placeholder="Caută produs..."
                        className="search-input"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                {/* Bara de Filtre și Sortare */}
                <div className="filters-bar">
                    {/* Filtru Brand */}
                    <select
                        className="filter-select"
                        value={selectedBrand}
                        onChange={(e) => setSelectedBrand(e.target.value)}
                    >
                        <option value="">Toate Brandurile</option>
                        {uniqueBrands.map(brand => (
                            <option key={brand} value={brand}>{brand}</option>
                        ))}
                    </select>

                    {/* Filtru Categorie */}
                    <select
                        className="filter-select"
                        value={selectedCategory}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                    >
                        <option value="">Toate Categoriile</option>
                        {uniqueCategories.map(cat => (
                            <option key={cat} value={cat}>{cat}</option>
                        ))}
                    </select>

                    {/* Sortare */}
                    <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center' }}>
                        <span className="sort-label">Sortează:</span>
                        <select
                            className="filter-select"
                            value={sortOrder}
                            onChange={(e) => setSortOrder(e.target.value)}
                        >
                            <option value="newest">Cele mai noi</option>
                            <option value="price-asc">Preț: Mic la Mare</option>
                            <option value="price-desc">Preț: Mare la Mic</option>
                        </select>
                    </div>
                </div>
            </div>

            {/* Grid-ul de Produse */}
            {filteredProducts.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '50px', color: '#64748b' }}>
                    <h3>Nu am găsit produse.</h3>
                    <p>Încearcă să schimbi filtrele sau termenul de căutare.</p>
                    <button
                        className="btn-add-cart"
                        style={{marginTop: '10px'}}
                        onClick={() => {
                            setSearchTerm('');
                            setSelectedBrand('');
                            setSelectedCategory('');
                        }}
                    >
                        Resetează Filtrele
                    </button>
                </div>
            ) : (
                <div className="products-grid">
                    {filteredProducts.map((product) => (
                        <ProductCard
                            key={product.id}
                            product={product}
                            onAddToCart={handleAddToCart}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}