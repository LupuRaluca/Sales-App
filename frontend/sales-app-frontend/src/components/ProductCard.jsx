import React from 'react';

export default function ProductCard({ product, onAddToCart }) {

    const stock = product.qty !== undefined ? product.qty : product.inventoryQuantityAvailable;
    let stockClass = "stock-ok";
    let stockText = "In Stoc";

    if (stock === 0) {
        stockClass = "stock-out";
        stockText = "Stoc Epuizat";
    } else if (stock < 5) {
        stockClass = "stock-low";
        stockText = "Stoc Limitat";
    }

    return (
        <div className="product-card">
            <div className={`stock-badge ${stockClass}`}>
                {stockText} ({stock})
            </div>

            <div className="card-image-placeholder">
                <span>{product.name ? product.name.charAt(0) : 'P'}</span>
            </div>

            <div className="card-content">
                <div className="card-badges">
                    <span className="badge badge-brand">{product.brandName}</span>
                    <span className="badge badge-category">{product.categoryName}</span>
                </div>

                <h3 className="product-name">{product.name}</h3>
                <p className="product-desc">{product.description}</p>

                <div className="card-footer">
                    <div className="price-wrapper">
                        <span className="price">
                            {product.priceWithVat} {product.currency}
                        </span>

                        <span style={{fontSize:'0.75rem', color:'#94a3b8', display:'block'}}>
                            (Net: {product.price})
                        </span>
                    </div>

                    <button
                        className="btn-add-cart"
                        disabled={stock === 0}
                        onClick={() => onAddToCart(product)}
                    >
                        {stock === 0 ? 'Indisponibil' : 'Adaugă'}
                    </button>
                </div>
            </div>
        </div>
    );
}