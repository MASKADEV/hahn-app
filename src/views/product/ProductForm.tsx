import { useNavigate, useParams } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useEffect } from "react";
import { useCreateProduct, useProduct, useUpdateProduct } from "../../api/productQueries.ts";
import type { UpdateProduct } from "../../types/Product.ts";
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { InputTextarea } from 'primereact/inputtextarea';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useRef } from 'react';
import { Card } from 'primereact/card';
import { classNames } from 'primereact/utils';

const productSchema = z.object({
    name: z.string().min(1, 'Name is required').max(255, 'Name too long'),
    description: z.string().max(1000, 'Description too long').optional(),
    price: z.number({ invalid_type_error: 'Price must be a number' })
        .min(0.01, 'Price must be greater than 0'),
    quantity: z.number({ invalid_type_error: 'Quantity must be a number' })
        .min(0, 'Quantity must be positive'),
});

type ProductFormData = z.infer<typeof productSchema>;

const ProductForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditing = !!id;
    const toast = useRef<Toast>(null);

    const { data: productData } = useProduct(Number(id));
    const product = productData?.data;

    const { mutate: createProduct } = useCreateProduct();
    const { mutate: updateProduct } = useUpdateProduct();

    const {
        control,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm<ProductFormData>({
        resolver: zodResolver(productSchema),
        defaultValues: {
            name: '',
            description: '',
            price: 0,
            quantity: 0,
        },
    });

    useEffect(() => {
        if (isEditing && product) {
            reset({
                name: product.name,
                description: product.description || '',
                price: product.price,
                quantity: product.quantity,
            });
        }
    }, [isEditing, product, reset]);

    const onSubmit = (data: ProductFormData) => {
        if (isEditing && id) {
            const updateData: UpdateProduct = { ...data, id: Number(id) };
            updateProduct(updateData, {
                onSuccess: () => {
                    toast.current?.show({
                        severity: 'success',
                        summary: 'Success',
                        detail: 'Product updated successfully',
                        life: 3000
                    });
                    navigate('/');
                },
                onError: () => {
                    toast.current?.show({
                        severity: 'error',
                        summary: 'Error',
                        detail: 'Failed to update product',
                        life: 3000
                    });
                }
            });
        } else {
            createProduct(data, {
                onSuccess: () => {
                    toast.current?.show({
                        severity: 'success',
                        summary: 'Success',
                        detail: 'Product created successfully',
                        life: 3000
                    });
                    navigate('/');
                },
                onError: () => {
                    toast.current?.show({
                        severity: 'error',
                        summary: 'Error',
                        detail: 'Failed to create product',
                        life: 3000
                    });
                }
            });
        }
    };

    const getFormErrorMessage = (name: keyof ProductFormData) => {
        return errors[name] && (
            <small className="p-error">{errors[name]?.message}</small>
        );
    };

    return (
        <div className="w-screen h-screen flex items-center justify-center">
            <Toast ref={toast} />
            <Card title={isEditing ? 'Edit Product' : 'Create New Product'} className="w-[60%]">
                <form onSubmit={handleSubmit(onSubmit)} className="p-fluid">
                    <div className="field mb-4">
                        <label htmlFor="name" className="block text-700 font-medium mb-2">
                            Name*
                        </label>
                        <Controller
                            name="name"
                            control={control}
                            render={({ field, fieldState }) => (
                                <InputText
                                    id={field.name}
                                    value={field.value}
                                    onChange={field.onChange}
                                    className={classNames({ 'p-invalid': fieldState.error })}
                                    placeholder="Enter product name"
                                />
                            )}
                        />
                        {getFormErrorMessage('name')}
                    </div>

                    <div className="field mb-4">
                        <label htmlFor="description" className="block text-700 font-medium mb-2">
                            Description
                        </label>
                        <Controller
                            name="description"
                            control={control}
                            render={({ field, fieldState }) => (
                                <InputTextarea
                                    id={field.name}
                                    value={field.value}
                                    onChange={field.onChange}
                                    rows={3}
                                    className={classNames({ 'p-invalid': fieldState.error })}
                                    placeholder="Enter product description"
                                />
                            )}
                        />
                        {getFormErrorMessage('description')}
                    </div>

                    <div className="field mb-4">
                        <label htmlFor="price" className="block text-700 font-medium mb-2">
                            Price*
                        </label>
                        <Controller
                            name="price"
                            control={control}
                            render={({ field, fieldState }) => (
                                <InputNumber
                                    id={field.name}
                                    value={field.value}
                                    onValueChange={(e) => field.onChange(e.value)}
                                    mode="currency"
                                    currency="USD"
                                    locale="en-US"
                                    className={classNames({ 'p-invalid': fieldState.error })}
                                />
                            )}
                        />
                        {getFormErrorMessage('price')}
                    </div>

                    <div className="field mb-4">
                        <label htmlFor="quantity" className="block text-700 font-medium mb-2">
                            Quantity*
                        </label>
                        <Controller
                            name="quantity"
                            control={control}
                            render={({ field, fieldState }) => (
                                <InputNumber
                                    id={field.name}
                                    value={field.value}
                                    onValueChange={(e) => field.onChange(e.value)}
                                    min={0}
                                    className={classNames({ 'p-invalid': fieldState.error })}
                                />
                            )}
                        />
                        {getFormErrorMessage('quantity')}
                    </div>

                    <div className="flex gap-3 mt-6">
                        <Button
                            type="submit"
                            label={isEditing ? 'Update Product' : 'Create Product'}
                            icon={isEditing ? 'pi pi-check' : 'pi pi-plus'}
                            className="p-button-raised"
                        />
                        <Button
                            type="button"
                            label="Cancel"
                            icon="pi pi-times"
                            className="p-button-raised p-button-secondary"
                            onClick={() => navigate('/')}
                        />
                    </div>
                </form>
            </Card>
        </div>
    );
};

export default ProductForm;