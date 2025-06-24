export interface JwtToken {
    accessToken: string;
    refreshToken: string;
    user: User;
}

export interface User {
    id?: number;
    username?: string;
    email?: string;
    password?: string;
    roles: string[];
}
