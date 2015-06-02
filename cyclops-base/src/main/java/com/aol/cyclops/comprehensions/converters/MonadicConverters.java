package com.aol.cyclops.comprehensions.converters;

import java.util.ServiceLoader;

import lombok.Getter;
import lombok.val;

import org.jooq.lambda.Seq;
import org.pcollections.PStack;

import com.aol.cyclops.lambda.api.MonadicConverter;
import com.aol.cyclops.lambda.api.Reducers;
import com.aol.cyclops.streams.StreamUtils;

public class MonadicConverters {
	

	private final  StreamUpscaler upscaler = getConverter();
	private static StreamUpscaler getConverter() {
		try{
			Class.forName("org.jooq.lambda.Seq");
			return stream -> Seq.seq(stream);
		}catch(ClassNotFoundException e){
			return  stream -> stream;
		}
		
	}
	@Getter
	private final static PStack<MonadicConverter> converters;
	
	
	static {
		val loader  = ServiceLoader.load(MonadicConverter.class);
		converters = Reducers.<MonadicConverter>toPStack().mapReduce(StreamUtils.stream(loader.iterator()).sorted((a,b) ->  b.priority()-a.priority()));
		
	}
	
	
	//Supplier[] Callable[]  to LazyFutureStream
	//CompletableFuture[] to EagerFutureStream
	//CheckedSupplier to Try
	//CheckedSupplier[]  to Seq of Try
	//async.Queue, async.Topic, async.Signal to Seq / Stream
	
	
	
	
	
	public Object convertToMonadicForm(Object o){
		return upscaler.upscaleIfStream(converters.stream().filter(t-> t.accept(o)).map(m -> m.convertToMonadicForm(o)).findFirst().orElse(o));
	}
	
}