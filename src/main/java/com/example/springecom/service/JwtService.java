package com.example.springecom.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static String SECRET = "";

    public JwtService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            SECRET = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Key getkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // Token valid for 10 minutieses
                .signWith(getkey())
                .compact();

    }


    public String extractUsername(String token) {
        // Gọi hàm giải mã để lấy trường Subject (nơi lưu username lúc generateToken)
        return extractClaim(token, Claims::getSubject);

        //cach 2
//        Claims claims = extractAllClaims(token); // Tốn công giải mã lần 1
//        return claims.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // Bước A: Trích xuất tên người dùng được giấu bên trong chuỗi JWT
        final String usernameInToken = extractUsername(token);

        // Bước B: Đối chiếu xem username trong token có khớp với dữ liệu User thật dưới DB không
        // Đồng thời kiểm tra xem token này đã quá hạn dùng (3 phút / 30 phút) hay chưa
        return (usernameInToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Hàm trích xuất 1 thông tin (Claim) bất kỳ từ Token bằng Functional Interface của Java 8
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token); //extract all claim tu token

        return  claimsResolver.apply(claims);

    }

    // Hàm bóc tách ngày hết hạn được ghi trong Payload của JWT
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Hàm kiểm tra thời gian: Trả về true nếu thời gian hết hạn đứng TRƯỚC thời gian hiện tại
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Hàm phân tích cú pháp (Parse) trần trụi chuỗi JWT sử dụng verifyWith của JJWT mới
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getkey())
                .build()
                .parseSignedClaims(token)    // Tự động check chữ ký chống hacker sửa dữ liệu
                .getPayload();               // Bốc lấy phần ruột dữ liệu (Claims) trả về
    }
}
