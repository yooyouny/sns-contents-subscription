package com.postype.sns.domain.post.api;

import com.postype.sns.domain.member.dto.MemberDto;
import com.postype.sns.domain.post.dto.request.PostCreateRequest;
import com.postype.sns.domain.post.dto.request.PostModifyRequest;
import com.postype.sns.domain.post.dto.response.CommentResponse;
import com.postype.sns.domain.post.dto.response.PostResponse;
import com.postype.sns.global.common.Response;
import com.postype.sns.domain.post.application.PostUseCase;
import com.postype.sns.domain.post.dto.PostDto;
import com.postype.sns.domain.post.application.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final PostUseCase createPostUseCase;

	@Operation(summary = "포스트 발행", description = "로그인한 멤버가 title, body, price를 작성하여 포스트를 발행합니다.")
	@PostMapping
	public Response<Void> create(@Valid @RequestBody PostCreateRequest request, @ApiIgnore @AuthenticationPrincipal MemberDto memberDto){
		createPostUseCase.execute(request, memberDto);
		return Response.success();
	}
	@Operation(summary = "포스트 수정", description = "로그인한 멤버가 변경된 내용으로 포스트를 수정합니다")
	@PutMapping("/{postId}")
	public Response<PostResponse> modify(@PathVariable Long postId, @Valid @RequestBody PostModifyRequest request,
		@AuthenticationPrincipal MemberDto memberDto){
		PostDto post = postService.modify(postId, request, memberDto);
		return Response.success(PostResponse.fromPostDto(post));
	}

	@Operation(summary = "포스트 삭제", description = "로그인한 멤버가 postId에 해당하는 포스트를 삭제합니다")
	@DeleteMapping("/{postId}")
	public Response<Void> delete(@PathVariable Long postId, @ApiIgnore @AuthenticationPrincipal MemberDto memberDto){
		postService.delete(memberDto, postId);
		return Response.success();
	}

	@Operation(summary = "인기 포스트 목록 가져오기", description = "한달 동안 가장 많은 좋아요를 받은 포스트 목록을 반환합니다")
	@GetMapping //TODO :: 오프셋 기반 좋아요 순 으로 정렬 limit 30 size 10, 등록일자가 최근 한달간 작성한 포스트로 제한
	public Response<Page<PostResponse>> getPostListSortedByLike(Pageable pageable){
		return Response.success(postService.getList(pageable)
				.map(PostResponse::fromPostDto));
	}

	@Operation(summary = "작성한 포스트 목록 가져오기", description = "로그인한 멤버가 발행한 최신 포스트 목록을 가져옵니다")
	@GetMapping("/my") //TODO :: 오프셋 기반 timestamp 내림차순으로 정렬
	public Response<Page<PostResponse>> getMyPostList(Pageable pageable, @ApiIgnore @AuthenticationPrincipal MemberDto memberDto){
		return Response.success(postService.getMyPostList(memberDto, pageable)
				.map(PostResponse::fromPostDto));
	}
	@Operation(summary = "좋아요 표시", description = "로그인한 멤버가 postId에 해당하는 포스트에 좋아요 표시를 합니다")
	@PostMapping("{postId}/likes")
	public Response<Void> like(@PathVariable Long postId, @ApiIgnore @AuthenticationPrincipal MemberDto memberDto){
		postService.like(postId, memberDto);
		return Response.success();
	}
	@Operation(summary = "좋아요 수 가져오기", description = "postId가 받은 좋아요 수를 가져옵니다")
	@GetMapping("{postId}/likes")
	public Response<Long> getLike(@PathVariable Long postId){
		return Response.success(postService.getLikeCount(postId));
	}

	@Operation(summary = "좋아요 한 포스트 목록 가져오기", description = "로그인한 멤버가 좋아요 표시 한 포스트 목록을 가져옵니다")
	@GetMapping("/likes")
	public Response<Page<PostResponse>> getMyLikePosts(@ApiIgnore @AuthenticationPrincipal MemberDto memberDto, Pageable pageable){
		return Response.success(postService.getLikeByMember(memberDto, pageable)
				.map(PostResponse::fromPostDto));
	}

	@Operation(summary = "코멘트 등록하기", description = "로그인한 멤버가 해당 postId에 코멘트를 등록합니다")
	@PostMapping("{postId}/comments")
	public Response<Void> comment(@PathVariable Long postId,
								  @RequestBody @NotBlank(message = "코멘트 내용은 필수입니다") String comment,
								  @AuthenticationPrincipal MemberDto memberDto){
		postService.comment(postId, memberDto, comment);
		return Response.success();
	}
	@Operation(summary = "코멘트 가져오기", description = "postId에 해당하는 포스트에 등록된 코멘트 목록 가져오기")
	@GetMapping("{postId}/comments")
	public Response<Page<CommentResponse>> getCommentByPost(@PathVariable Long postId, Pageable pageable){
		return Response.success(postService.getComment(postId, pageable)
				.map(CommentResponse::fromDto));
	}
}
