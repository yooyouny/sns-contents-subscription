package com.postype.sns.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Response<T> {
	private String resultCode;
	private T result;

	public Response(String resultCode, T result){
		this.resultCode = resultCode;
		this.result = result;
	}

	public static <T> Response<T> error(String errorCode){
		return new Response<>(errorCode, null);
	}

	public static <T> Response<T> success(T result){
		return new Response<>("SUCCESS", result);
	}

	public static <T> Response<Void> success(){
		return new Response<>("SUCCESS", null);
	}

	public String toStream() {
		if (result == null) {
			return "{" +
				"\"resultCode\":" + "\"" + resultCode + "\"," +
				"\"result\":" + null +
				"}";
		}
		return "{" +
			"\"resultCode\":" + "\"" + resultCode + "\"," +
			"\"result\":" + "\"" + result + "\"," +
			"}";
	}
}
