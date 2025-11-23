package com.worktime.assist.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // Timezone do Brasil (America/Sao_Paulo)
    private static final ZoneId BRAZIL_TIMEZONE = ZoneId.of("America/Sao_Paulo");

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        return builder
                .modules(new JavaTimeModule(), module)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    private static class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
        private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        public LocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            boolean isUtc = false;
            String offset = null;
            
            // Verifica se tem timezone UTC (Z)
            if (text.endsWith("Z")) {
                isUtc = true;
                text = text.substring(0, text.length() - 1);
            } else {
                // Verifica se tem offset timezone (ex: +03:00, -05:00)
                int plusIndex = text.lastIndexOf('+');
                int minusIndex = text.lastIndexOf('-');
                int offsetIndex = Math.max(plusIndex, minusIndex);
                
                // Verifica se é um offset (tem ':' após o sinal e está após a data/hora)
                if (offsetIndex > 10) { // Evita remover o hífen da data
                    String afterOffset = text.substring(offsetIndex);
                    if (afterOffset.matches("[+-]\\d{2}:\\d{2}")) {
                        offset = afterOffset;
                        text = text.substring(0, offsetIndex);
                    }
                }
            }
            
            // Remove milissegundos se presente (ex: .107)
            if (text.contains(".")) {
                int dotIndex = text.indexOf('.');
                text = text.substring(0, dotIndex);
            }
            
            // Se for UTC, converte para o horário do Brasil
            if (isUtc) {
                // Parse como Instant (UTC) e converte para o timezone do Brasil
                // Formato: yyyy-MM-ddTHH:mm:ss
                LocalDateTime localDateTime = LocalDateTime.parse(text, ISO_FORMATTER);
                // Assumindo que o texto recebido já estava em UTC, convertemos para o timezone do Brasil
                ZonedDateTime utcZoned = localDateTime.atZone(ZoneId.of("UTC"));
                ZonedDateTime brazilZoned = utcZoned.withZoneSameInstant(BRAZIL_TIMEZONE);
                return brazilZoned.toLocalDateTime();
            }
            
            // Se tiver offset, converte para o timezone do Brasil
            if (offset != null) {
                String zonedText = text + offset;
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedText, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                ZonedDateTime brazilTime = zonedDateTime.withZoneSameInstant(BRAZIL_TIMEZONE);
                return brazilTime.toLocalDateTime();
            }
            
            // Se não tiver timezone, assume que já está no horário do Brasil
            return LocalDateTime.parse(text, ISO_FORMATTER);
        }
    }
}
