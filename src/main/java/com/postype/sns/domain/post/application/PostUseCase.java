package com.postype.sns.domain.post.application;

import com.postype.sns.domain.member.dto.FollowDto;
import com.postype.sns.domain.member.dto.MemberDto;
import com.postype.sns.domain.member.application.FollowService;
import com.postype.sns.domain.timeline.application.TimeLineService;
import java.util.List;
import java.util.stream.Collectors;

import com.postype.sns.domain.post.dto.request.PostCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostUseCase {

	private final PostService postService;
	private final FollowService followService;
	private final TimeLineService timeLineService;

	//TODO :: 트랜잭션 걸지 않을 거임 팔로워 많을 경우 과부하
	public Long execute(PostCreateRequest request, MemberDto memberDto){
		Long postId = postService.create(request, memberDto);
		List<Long> followedMemberIds = followService.getFollowers(memberDto).stream()
			.map(FollowDto::getFromMemberId)
			.collect(Collectors.toList());
		timeLineService.deliveryToTimeLine(postId, followedMemberIds);
		return postId;
	}




}
