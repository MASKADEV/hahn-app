import { useNavigate } from 'react-router-dom';
import { useAuth } from "../../context/AuthContext.tsx";
import { Card } from 'primereact/card';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { classNames } from 'primereact/utils';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, Controller } from 'react-hook-form';
import * as z from 'zod';
import { Link } from 'react-router-dom';

const registerSchema = z.object({
    username: z.string()
        .min(3, 'Username must be at least 3 characters')
        .max(20, 'Username must be less than 20 characters'),
    email: z.string()
        .email('Invalid email address'),
    password: z.string()
        .min(6, 'Password must be at least 6 characters')
        .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
        .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
        .regex(/[0-9]/, 'Password must contain at least one number'),
    confirmPassword: z.string()
}).refine(data => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"]
});

type RegisterFormData = z.infer<typeof registerSchema>;

export const Register = () => {
    const { signup, isLoading } = useAuth();
    const navigate = useNavigate();

    const {
        control,
        handleSubmit,
        formState: { errors, isSubmitSuccessful },
        setError: setFormError,
    } = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            username: '',
            email: '',
            password: '',
            confirmPassword: ''
        }
    });

    const onSubmit = async (data: RegisterFormData) => {
        try {
            await signup({
                username: data.username,
                email: data.email,
                password: data.password
            });
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setFormError('root', {
                type: 'manual',
                message: err instanceof Error ? err.message : 'Registration failed'
            });
        }
    };

    return (
        <div className="flex align-items-center justify-content-center min-h-screen">
            <Card
                title="Create Account"
                className="w-full md:w-30rem"
            >
                {isSubmitSuccessful ? (
                    <Message
                        severity="success"
                        text="Registration successful! Redirecting to login..."
                    />
                ) : (
                    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-column gap-3">
                        {errors.root && (
                            <Message
                                severity="error"
                                text={errors.root.message}
                                className="w-full"
                            />
                        )}

                        <div className="flex flex-column gap-2">
                            <label htmlFor="username">Username</label>
                            <Controller
                                name="username"
                                control={control}
                                render={({ field, fieldState }) => (
                                    <>
                                        <InputText
                                            id={field.name}
                                            value={field.value}
                                            onChange={field.onChange}
                                            className={classNames({ 'p-invalid': fieldState.error })}
                                        />
                                        {fieldState.error && (
                                            <small className="p-error">{fieldState.error.message}</small>
                                        )}
                                    </>
                                )}
                            />
                        </div>

                        <div className="flex flex-column gap-2">
                            <label htmlFor="email">Email</label>
                            <Controller
                                name="email"
                                control={control}
                                render={({ field, fieldState }) => (
                                    <>
                                        <InputText
                                            id={field.name}
                                            value={field.value}
                                            onChange={field.onChange}
                                            className={classNames({ 'p-invalid': fieldState.error })}
                                        />
                                        {fieldState.error && (
                                            <small className="p-error">{fieldState.error.message}</small>
                                        )}
                                    </>
                                )}
                            />
                        </div>

                        <div className="flex flex-column gap-2">
                            <label htmlFor="password">Password</label>
                            <Controller
                                name="password"
                                control={control}
                                render={({ field, fieldState }) => (
                                    <>
                                        <Password
                                            id={field.name}
                                            value={field.value}
                                            onChange={field.onChange}
                                            toggleMask
                                            feedback
                                            className={classNames({ 'p-invalid': fieldState.error })}
                                            inputClassName="w-full"
                                        />
                                        {fieldState.error && (
                                            <small className="p-error">{fieldState.error.message}</small>
                                        )}
                                    </>
                                )}
                            />
                        </div>

                        <div className="flex flex-column gap-2">
                            <label htmlFor="confirmPassword">Confirm Password</label>
                            <Controller
                                name="confirmPassword"
                                control={control}
                                render={({ field, fieldState }) => (
                                    <>
                                        <Password
                                            id={field.name}
                                            value={field.value}
                                            onChange={field.onChange}
                                            toggleMask
                                            className={classNames({ 'p-invalid': fieldState.error })}
                                            inputClassName="w-full"
                                        />
                                        {fieldState.error && (
                                            <small className="p-error">{fieldState.error.message}</small>
                                        )}
                                    </>
                                )}
                            />
                        </div>

                        <Button
                            type="submit"
                            label="Register"
                            icon="pi pi-user-plus"
                            loading={isLoading}
                            className="mt-3"
                        />

                        <div className="text-center mt-3">
                            <span className="text-sm text-gray-600">Already have an account? </span>
                            <Link
                                to="/login"
                                className="text-sm text-blue-500 hover:text-blue-700 font-medium"
                            >
                                Login here
                            </Link>
                        </div>
                    </form>
                )}
            </Card>
        </div>
    );
};