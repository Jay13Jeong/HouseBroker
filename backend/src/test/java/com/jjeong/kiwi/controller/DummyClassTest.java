package com.jjeong.kiwi.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class DummyClassTest {

    @Autowired
    private DummyClass dummyClass;

    @Test
    void hello() {
        assertThat(dummyClass.hello()).isEqualTo("hello");
    }
}