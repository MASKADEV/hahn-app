export interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    quantity: number;
    createdAt: string;
    updatedAt: string;
    active: boolean;
}

export interface CreateProduct {
    name: string;
    description: string | undefined;
    price: number;
    quantity: number;
}

export interface UpdateProduct extends Partial<CreateProduct> {
    id: number;
}