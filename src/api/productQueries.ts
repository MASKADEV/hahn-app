import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from "./axiosConfig.ts";
import type {CreateProduct, Product, UpdateProduct} from "../types/Product.ts";
import type {ApiResponse} from "../types/common.ts";

export const useProducts = () => {
    return useQuery<ApiResponse<Product[]>>({
        queryKey: ['products'],
        queryFn: () => apiClient.get('/api/products').then(res => res.data),
    });
};

export const useProduct = (id: number) => {
    return useQuery<ApiResponse<Product>>({
        queryKey: ['product', id],
        queryFn: () => apiClient.get(`/api/products/${id}`).then(res => res.data),
        enabled: !!id,
    });
};

export const useCreateProduct = () => {
    const queryClient = useQueryClient();

    return useMutation<
        ApiResponse<Product>,
        Error,
        CreateProduct
    >({
        mutationFn: (product) =>
            apiClient.post('/api/products', product).then(res => res.data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
};

export const useUpdateProduct = () => {
    const queryClient = useQueryClient();

    return useMutation<
        ApiResponse<Product>,
        Error,
        UpdateProduct
    >({
        mutationFn: ({ id, ...product }) =>
            apiClient.put(`/api/products/${id}`, product).then(res => res.data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
            queryClient.invalidateQueries({ queryKey: ['product', variables.id] });
        },
    });
};

export const useDeleteProduct = () => {
    const queryClient = useQueryClient();

    return useMutation<ApiResponse<void>, Error, number>({
        mutationFn: (id) =>
            apiClient.delete(`/api/products/${id}`).then(res => res.data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
};