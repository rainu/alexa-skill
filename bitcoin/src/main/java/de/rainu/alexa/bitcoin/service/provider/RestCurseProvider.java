package de.rainu.alexa.bitcoin.service.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rainu.alexa.bitcoin.model.BitcoinCurse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestCurseProvider implements BitcoinCurseProvider {
  private static final Logger log = LoggerFactory.getLogger(RestCurseProvider.class);

  public static final String ENDPOINT = "https://bitcoinapi.de/widget/current-btc-price/rate.json?culture=de";

  private static final Pattern MONEY_PATTERN = Pattern.compile("([0-9\\.,]*)");

  @Autowired
  RestTemplate rest;

  @Autowired
  ObjectMapper mapper;

  DateTime lastCall;
  BitcoinCurse lastCurse;

  @Override
  public BitcoinCurse getCurrentCurse() {
    if(lastCurse == null || ttlIsOver()) {
      try {
        lastCurse = requestBitcoinCurse();
      } catch (IOException e) {
        log.error("Error while request bitcoin!", e);
        lastCurse = new BitcoinCurse(DateTime.now().minusDays(1), null, null);
      }
      lastCall = DateTime.now();
    }

    return lastCurse;
  }

  private boolean ttlIsOver() {
    return Math.abs(new Duration(DateTime.now(), lastCall).getStandardMinutes()) >= 1;
  }

  private BitcoinCurse requestBitcoinCurse() throws IOException {
    ResponseEntity<String> response = rest.getForEntity(ENDPOINT, String.class);
    Response entity = mapper.readValue(response.getBody(), Response.class);

    return convert(entity);
  }

  private BitcoinCurse convert(Response raw) {
    final Double coin = 1d;
    Double euro = 0d;

    Matcher matcher = MONEY_PATTERN.matcher(raw.price_eur);
    if(matcher.find()) {
      final String rawEuro = matcher.group(1)
          .replace(".", ";")
          .replace(",", ".")
          .replace(";", "");

      euro = Double.parseDouble(rawEuro);
    }

    final DateTime date = DateTimeFormat.forPattern("dd.MM.yy HH:mm").parseDateTime(raw.date_de);

    return new BitcoinCurse(date, coin, euro);
  }

  public static class Response {
    @JsonProperty("price_eur")
    private String price_eur;

    @JsonProperty("date_de")
    private String date_de;

    @JsonProperty("text")
    private String text;
  }
}
