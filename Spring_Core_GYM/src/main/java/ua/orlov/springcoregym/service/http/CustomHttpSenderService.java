package ua.orlov.springcoregym.service.http;

import ua.orlov.springcoregym.model.HttpRequest;

public interface CustomHttpSenderService {

    String executeRequestWithEntity(HttpRequest request, String entity);

}
