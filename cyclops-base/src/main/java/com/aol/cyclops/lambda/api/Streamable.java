package com.aol.cyclops.lambda.api;

import static com.aol.cyclops.lambda.api.AsDecomposable.asDecomposable;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Streamable<T> extends Iterable<T>{

	default Iterator<T> iterator(){
		return stream().iterator();
	}
	default  Object getStreamable(){
		return this;
	}
	
	default Stream<T> stream(){
		Object streamable = getStreamable();
		if(streamable instanceof Stream)
			return (Stream)streamable;
		if(streamable instanceof Iterable)
			return StreamSupport.stream(((Iterable)streamable).spliterator(), false);
		return  new InvokeDynamic().stream(streamable).orElseGet( ()->
								(Stream)StreamSupport.stream(asDecomposable(streamable)
												.unapply()
												.spliterator(),
													false));
	}
	
	public static<T> Streamable<T> of(T... values){
		return new Streamable<T>(){
			public Stream<T> stream(){
				return Stream.of(values);
			}
		};
	}
}
