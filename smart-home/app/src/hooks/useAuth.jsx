import React, { useState, useEffect, useContext, createContext } from 'react';

const authContext = createContext();

export function AuthProvider({ children }) {
    const auth = useProvideAuth()
    return <authContext.Provider value={auth}>{children}</authContext.Provider>
}

export const useAuth = () => {
    return useContext(authContext);
}

function useProvideAuth() {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    const handleUser = (rawUser) => {
        if (rawUser) {
            const user = formatUser(rawUser)
            setLoading(false)
            setUser(user)
            return user
        } else {
            setLoading(false)
            setUser(false)
            return false
        }
    }

    const signIn = () => {
    }

    const signOut = () => {
    }

    useEffect(() => {
        fetch(`/app/auth`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                handleUser(data);
            })
    }, [])

    return {
        user,
        loading,
        signIn,
        signOut,
    }
}

const formatUser = (user) => {
    return {
        authenticated: user.authenticated,
        name: user.name
    }
}