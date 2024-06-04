package com.jbp.front.controller;

import com.yeepay.yop.sdk.security.DigitalEnvelopeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class YopController {

    @RequestMapping("/yop/{type}")
    public String yopCallback(@PathVariable("type") String type, String response) {
        log.info("获取易宝回调参数, {}", response);
        log.info("获取易宝回调解密参数, {}", DigitalEnvelopeUtils.decrypt(response, "RSA2048"));
        // 处理业务逻辑
        return "FAIL";
    }

    public static void main(String[] args) {
        String str = "R1nxH7GqR7WsG-KW-8WbLDid7l7xbGnHtS9W5FE5sF69_hxSrGs0vU900GWqxXhFQp-joFG9iS4PXfnWUavvrU9fOjw-Dxbdri101WXVGuGRRkc1QoAJnU2e0m3I04pwlOAomtTqhYxded2IT4UTrOQa2TxcRH_ShfFPJ9NVXByi9R02xrLfQg0Eb1mGyJlW-AWAUe1aJammV2VFiF1G3cNJjj5KhETJoV3ySY5sk66uYmAufyrpwHkefts1mKFmVgf-jyWmy5MWZeGmAwdW56mU8ONN_MEUylKvgXo81nf_k6WvWe_UVsGplVdePkjIwz5jcXkscsRmxQqlO6ia-w%24BqOtPnLSeWd_vKq1KN0mmAW0ncZ8Vz2JfR1yUOHvJJGzBfm5mwORWYQfxZAahN3mZahlquyxirVR_BJUOK1qfvspSuRa1s_YaC7mR57glfME8-3-VeHA3RjKabOzoWqXQBWBvS8EbLnNgY7HMddIvtNh-YiiXtkygZ6Fca7Ry5c6pAsgKLvYRt-GQuL9qEmi_ViTdgK2qs5W1_-1o5UKAFyi1QGpN5uyLA8lMCY1W3MkandaZlqFT1tO-vBc8-NhYL7dfIBkgpOOTYf-rN7uIbP_TI700C7PR8mdXbzsnJpGC2SAdOROXt9qnOwsTWLS725Z9wGF7X8p_lKetKXYs33PjKn0fs1atR67sVA-BEcWXgQyGdk1g3WUfifpYJUsQoWh_lH-u0h8JOdYe2pnH8SuEeohSnvCcREcsV2yxqvzq_cm0hoAE0V5UYqV0GxyNfT6YyCVZR0nz7RmyLzzhd7Qm7EqQrpE7Bw47xuqQw24QjlB9gTuM5zBLM-uTR2Ddq38rWdKjktNoAOiM_2VqTyv90EgruLFVWdAcJeC20dqbpegUCxv2IKFY7CK8ant0kASlD1CFg5p-SGAm2uNDNxvWP6Np--AgKvGEdBupNQ90aIWzovDT7g_ttJ7kBXxFnI5fnS_Y64u93qSzANVYA%24AES%24SHA256&customerIdentification=app_10089066338";
        System.out.println(DigitalEnvelopeUtils.decrypt(str, "RSA2048"));
    }

}
