import type {User} from "./authentication.ts";

export interface ApiResponse<T> {
    message: string;
    data: T;
    success: boolean;
}

export interface AuthContextType {
    user: User;
    isLoading: boolean;
    login: (credentials: { username: string; password: string }) => Promise<void>;
    signup: (data: { username: string; email: string; password: string }) => Promise<void>;
    logout: () => Promise<void>;
}