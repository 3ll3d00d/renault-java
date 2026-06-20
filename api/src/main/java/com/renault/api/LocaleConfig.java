package com.renault.api;

import java.util.Map;
import java.util.Optional;

/// Maps locale strings to Gigya and Kamereon API credentials. Pass a locale to
/// {@link RenaultClient#RenaultClient(String)} to select the right regional endpoints.
///
/// Supported locales (BCP 47 format with underscore separator):
/// `bg_BG`, `cs_CZ`, `da_DK`, `de_AT`, `de_CH`, `de_DE`,
/// `en_GB`, `en_IE`, `es_ES`, `es_MX`, `fi_FI`, `fr_BE`,
/// `fr_CH`, `fr_FR`, `fr_LU`, `hr_HR`, `hu_HU`, `it_CH`,
/// `it_IT`, `nl_BE`, `nl_NL`, `no_NO`, `pl_PL`, `pt_PT`,
/// `ro_RO`, `ru_RU`, `sk_SK`, `sl_SI`, `sv_SE`.
///
/// All EU locales share the same Gigya and Kamereon endpoints; `es_MX` uses US endpoints.
public final class LocaleConfig {
    public static final String GIGYA_URL_EU      = "https://accounts.eu1.gigya.com";
    public static final String GIGYA_URL_US      = "https://accounts.us1.gigya.com";
    public static final String GIGYA_API_KEY_EU  = "3_VgdkgtIRH3AdHvJm-cjV2ug2EFE0lxt0IJzMC4MFqZjFpn_GYFXVdNZ19L7wZX0N";
    public static final String KAMEREON_URL_EU   = "https://api-wired-prod-1-euw1.wrd-aws.com";
    public static final String KAMEREON_URL_US   = "https://api-wired-prod-1-usw2.wrd-aws.com";
    public static final String KAMEREON_API_KEY  = "YjkKtHmGfaceeuExUDKGxrLZGGvtVS0J";

    public record Credentials(
        String gigyaUrl,
        String gigyaApiKey,
        String kamereonUrl,
        String kamereonApiKey,
        String country
    ) {}

    private static final Map<String, Credentials> LOCALES = Map.ofEntries(
        locale("bg_BG", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("cs_CZ", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("da_DK", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("de_DE", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("de_AT", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("de_CH", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("en_GB", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("en_IE", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("es_ES", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("es_MX", GIGYA_URL_US, "4_yTFqPSsGxVyRXPZUM7t1Iw", KAMEREON_URL_US),
        locale("fi_FI", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("fr_FR", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("fr_BE", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("fr_CH", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("fr_LU", GIGYA_URL_EU, "3_zt44Wl_wT9mnqn-BHrR19PvXj3wYRPQKLcPbGWawlatFR837KdxSZZStbBTDaqnb", KAMEREON_URL_EU),
        locale("hr_HR", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("hu_HU", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("it_IT", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("it_CH", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("nl_NL", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("nl_BE", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("no_NO", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("pl_PL", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("pt_PT", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("ro_RO", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("ru_RU", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("sk_SK", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("sl_SI", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU),
        locale("sv_SE", GIGYA_URL_EU, GIGYA_API_KEY_EU, KAMEREON_URL_EU)
    );

    /// Returns credentials for the given locale, or empty if the locale is not supported.
    public static Optional<Credentials> forLocale(String locale) {
        return Optional.ofNullable(LOCALES.get(locale));
    }

    /// Returns credentials for the given locale.
    ///
    /// @throws IllegalArgumentException if the locale is not supported
    public static Credentials requireForLocale(String locale) {
        return forLocale(locale).orElseThrow(() ->
            new IllegalArgumentException("Unsupported locale: %s. Supported: %s".formatted(locale, LOCALES.keySet())));
    }

    private static Map.Entry<String, Credentials> locale(String locale, String gigyaUrl, String gigyaKey, String kamereonUrl) {
        String country = locale.substring(locale.indexOf('_') + 1);
        return Map.entry(locale, new Credentials(gigyaUrl, gigyaKey, kamereonUrl, KAMEREON_API_KEY, country));
    }

    private LocaleConfig() {}
}
