import { useNavigate } from 'react-router-dom';
import { useProducts, useDeleteProduct } from "../api/productQueries.ts";
import type {Product} from "../types/Product.ts";
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Toast } from 'primereact/toast';
import { useRef } from 'react';
import { useCurrentUser, useLogout } from "../api/authQueries.ts";
import 'primereact/resources/themes/lara-light-indigo/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

const Home = () => {
    const navigate = useNavigate();
    const { data, isLoading, error } = useProducts();
    const { mutate: deleteProduct } = useDeleteProduct();
    const { mutate: logout } = useLogout();
    const { data: currentUser } = useCurrentUser();
    const toast = useRef<Toast>(null);

    const products = data?.data || [];

    const handleEdit = (id: number) => {
        navigate(`/products/edit/${id}`);
    };

    const handleCreate = () => {
        navigate('/products/new');
    };

    const handleLogout = () => {
        logout(undefined, {
            onSuccess: () => {
                navigate('/login');
            },
            onError: () => {
                toast.current?.show({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'Failed to logout',
                    life: 3000
                });
            }
        });
    };

    const handleDelete = (id: number) => {
        confirmDialog({
            message: 'Are you sure you want to delete this product?',
            header: 'Confirm Deletion',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                deleteProduct(id, {
                    onSuccess: () => {
                        toast.current?.show({
                            severity: 'success',
                            summary: 'Success',
                            detail: 'Product deleted successfully',
                            life: 3000
                        });
                    },
                    onError: () => {
                        toast.current?.show({
                            severity: 'error',
                            summary: 'Error',
                            detail: 'Failed to delete product',
                            life: 3000
                        });
                    }
                });
            }
        });
    };

    const priceBodyTemplate = (product: Product) => {
        return `$${product.price.toFixed(2)}`;
    };

    const actionBodyTemplate = (product: Product) => {
        return (
            <div className="flex gap-2">
                <Button
                    icon="pi pi-pencil"
                    className="p-button-rounded p-button-success p-button-sm"
                    onClick={() => handleEdit(product.id)}
                />
                <Button
                    icon="pi pi-trash"
                    className="p-button-rounded p-button-danger p-button-sm"
                    onClick={() => handleDelete(product.id)}
                />
            </div>
        );
    };

    if (isLoading) return <div className="flex justify-center items-center h-screen">Loading products...</div>;
    if (error) return <div className="flex justify-center items-center h-screen">Error loading products: {error.message}</div>;

    return (
        <div className="container mx-auto px-4 py-8">
            <Toast ref={toast} />
            <ConfirmDialog />
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-3xl font-bold">Product Management</h1>
                    {currentUser && (
                        <p className="text-sm text-gray-600 mt-1">
                            Welcome, {currentUser.username || currentUser.email}
                        </p>
                    )}
                </div>
                <div className="flex gap-3">
                    <Button
                        label="Create New Product"
                        icon="pi pi-plus"
                        onClick={handleCreate}
                        className="p-button-raised"
                    />
                    <Button
                        label="Logout"
                        icon="pi pi-sign-out"
                        onClick={handleLogout}
                        className="p-button-raised p-button-secondary"
                        severity="warning"
                    />
                </div>
            </div>

            <div className="card">
                <DataTable
                    value={products}
                    paginator
                    rows={10}
                    rowsPerPageOptions={[5, 10, 25, 50]}
                    tableStyle={{ minWidth: '50rem' }}
                    loading={isLoading}
                    emptyMessage="No products found."
                >
                    <Column field="name" header="Name" sortable></Column>
                    <Column field="description" header="Description" sortable></Column>
                    <Column field="price" header="Price" body={priceBodyTemplate} sortable></Column>
                    <Column field="quantity" header="Quantity" sortable></Column>
                    <Column header="Actions" body={actionBodyTemplate}></Column>
                </DataTable>
            </div>
        </div>
    );
};

export default Home;