package de.rainu.alexa.bitcoin.service.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.rainu.alexa.bitcoin.model.BitcoinCurse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestCurseProviderTest {

  @Autowired
  RestCurseProvider provider;

  @Test
  public void request(){
    //given
    //when
    BitcoinCurse currentCurse = provider.getCurrentCurse();


    //then
    assertNotNull(currentCurse.getDate());
    assertNotNull(currentCurse.getCoin());
    assertNotNull(currentCurse.getEuro());

    //check cache
    BitcoinCurse currentCurse2 = provider.getCurrentCurse();

    assertSame(currentCurse, currentCurse2);
  }
}
