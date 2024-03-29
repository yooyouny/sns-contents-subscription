package com.postype.sns.domain.post.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@NoArgsConstructor
public class PostModifyRequest {
	@NotBlank(message = "포스트 제목을 입력해주세요")
	private String title;

	@NotBlank(message = "포스트 내용을 입력해주세요")
	private String body;

	@PositiveOrZero
	private Integer price;

	@Builder
	public PostModifyRequest(String title, String body, int price) {
		this.title = title;
		this.body = body;
		this.price = price;
	}
}
