package com.google.gwtmockito.fakes;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Provides fake implementations of {@link SimpleBeanEditorDriver}.
 * 
 * TODO JDS Later it would be useful to dynamically create an instance variable which is set by
 * {@link SimpleBeanEditorDriver#edit(Object)} and retrieved by {@link SimpleBeanEditorDriver#flush()}
 * 
 * TODO JDS Possibly use a captor to capture the edited type and return it on flush.
 * 
 * @author jstroot
 * 
 */
public class FakeSimpleBeanEditorDriverProvider implements FakeProvider<SimpleBeanEditorDriver<?, ?>> {

    private final boolean hasEditorErrors;

    public FakeSimpleBeanEditorDriverProvider(boolean hasEditorErrors) {
        this.hasEditorErrors = hasEditorErrors;
    }

    @Override
    public SimpleBeanEditorDriver<?, ?> getFake(final Class<?> type) {
        return (SimpleBeanEditorDriver<?, ?>)Proxy.newProxyInstance(FakeSimpleBeanEditorDriverProvider.class.getClassLoader(), new Class<?>[] {type}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Class<?> uiRootType = getUiRootType(type);
                Class<?> returnType = method.getReturnType();
                if (returnType.isAssignableFrom(uiRootType)) {
                    return GWT.create(uiRootType);
                } else if (returnType == boolean.class) {
                    return hasEditorErrors;
                }
                return null;
            }
        });
    }

    private <T> Class<?> getUiRootType(Class<T> type) {
        ParameterizedType parameterizedType = (ParameterizedType)type.getGenericInterfaces()[0];
        Type uiRootType = parameterizedType.getActualTypeArguments()[0];
        if (uiRootType instanceof ParameterizedType) {
            return (Class<?>)((ParameterizedType)uiRootType).getRawType();
        } else {
            return (Class<?>)uiRootType;
        }
    }

}
