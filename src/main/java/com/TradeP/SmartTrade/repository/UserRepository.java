package com.TradeP.SmartTrade.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TradeP.SmartTrade.entity.User;

public interface UserRepository extends JpaRepository<User,UUID> {
    
}
