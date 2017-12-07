package controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import controllers.Wrap;

public class Wrap<T> {

	public Throwable throwable;
    public String message;
    public T entity;
    public boolean success;
    
    public static <T> Wrap<T> success(T entity){
        Wrap<T> w = new Wrap<>();
        w.entity = entity; w.success = true;
        return w;
    }
    
    public static <T> Wrap<T> handleError(Throwable t, String error){
        Wrap<T> w = new Wrap<>();
        w.throwable = t;
        w.message = error;
        w.success = false;
        return w;
    }

    public static <T> Wrap<T> handleError(Throwable t){
        Wrap<T> w = new Wrap<>();
        w.throwable = t;
        w.message = t.getMessage();
        w.success = false;
        return w;
    }
    
    public static <T,U> Wrap<T> propagate(Wrap<U> wrappedError){
        Wrap<T> w = new Wrap<>();
        w.throwable = wrappedError.throwable;
        w.message = wrappedError.message;
        return w;
    }
    
    public static <T,U> CompletionStage<Wrap<T>> propagateFuture(Wrap<U> wrappedError){
        Wrap<T> w = new Wrap<>();
        w.throwable = wrappedError.throwable;
        w.message = wrappedError.message;
        return CompletableFuture.completedFuture(w);
    }
}