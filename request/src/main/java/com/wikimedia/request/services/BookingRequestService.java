package com.wikimedia.request.services;

import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.basedomain.WikiData;
import com.wikimedia.request.entity.FakeUser;
import com.wikimedia.request.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingRequestService {


    @Autowired
    private UserRepository userRepository;

    public BookingRequest createBookingRequest(WikiData wikiData){
        FakeUser fakeUser = userRepository.findRandomUser();
        BookingRequest.BookingRequestBuilder bookBuilder = BookingRequest.builder();
        bookBuilder.userId(fakeUser.getUserId());
        bookBuilder.wikiId(wikiData.getId());
        return bookBuilder.build();
    }


}
