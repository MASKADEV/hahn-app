import {createContext, type ReactNode, useContext} from 'react';
import {useCurrentUser, useLogin, useLogout, useSignup} from '../api/authQueries';
import type {AuthContextType} from "../types/common.ts";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const { data: user, isLoading } = useCurrentUser();
    const { mutateAsync: loginMutation } = useLogin();
    const { mutateAsync: signupMutation } = useSignup();
    const { mutateAsync: logoutMutation } = useLogout();

    const login = async (credentials: { username: string; password: string }) => {
        await loginMutation(credentials);
    };

    const signup = async (data: { username: string; email: string; password: string }) => {
        await signupMutation(data);
    };

    const logout = async () => {
        await logoutMutation();
    };

    return (
        <AuthContext.Provider value={{ user, isLoading, login, signup, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};