import {useMutation, useQuery} from '@tanstack/react-query';
import apiClient from "./axiosConfig.ts";
import type {JwtToken, User} from "../types/authentication.ts";

let currentUser: User | null = null;

export const useLogin = () => {
    return useMutation<JwtToken, Error, Partial<User>>({
        mutationFn: async (credentials) => {
            const response = await apiClient.post('/api/auth/signin', credentials);
            return response.data.data;
        },
        onSuccess: (data) => {
            localStorage.setItem('token', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            if (data.user) {
                currentUser = data.user;
                localStorage.setItem('user', JSON.stringify(data.user));
            }
        },
    });
};

export const useSignup = () => {
    return useMutation<void, Error, Partial<User>>({
        mutationFn: (signupData) => apiClient.post('/api/auth/signup', signupData),
    });
};

export const useLogout = () => {
    return useMutation({
        mutationFn: () => {
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            currentUser = null;
            window.location.href = '/login';
            return Promise.resolve();
        },
    });
};

export const useCurrentUser = () => {
    return useQuery({
        queryKey: ['currentUser'],
        queryFn: () => {
            if (currentUser) return currentUser;

            const userStr = localStorage.getItem('user');
            if (userStr) {
                currentUser = JSON.parse(userStr);
                return currentUser;
            }

            return null;
        },
        enabled: !!localStorage.getItem('token')
    });
}