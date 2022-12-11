package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.OrderDTO;

public interface OrderMessagingService {

    void sendOrder(OrderDTO ordine);
}
