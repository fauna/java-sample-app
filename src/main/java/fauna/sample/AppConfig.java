package fauna.sample;

import com.fauna.client.Fauna;
import com.fauna.client.FaunaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfig {

    @Bean
    FaunaClient faunaClient() {
        // Creates a `FaunaClient` with its default configuration. Note that by
        // default it looks for `FAUNA_ENDPOINT` and `FAUNA_SECRET` environment
        // variables in oder to determine which endpoint and secret ot use. If
        // the endpoint is not set, it uses `https://db.fauna.com`.
        return Fauna.client();
    }
}