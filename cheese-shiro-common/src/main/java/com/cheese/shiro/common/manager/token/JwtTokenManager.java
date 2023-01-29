package com.cheese.shiro.common.manager.token;

import com.cheese.shiro.common.exception.TokenErrorException;
import com.cheese.shiro.common.exception.TokenExpiredException;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * JsonWebToken实现
 * @author sobann
 */
public class JwtTokenManager extends AbstractTokenManager {

	public  SecretKey generalKey(){
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] encodedKey = getKey().getBytes();
	    SecretKey signingKey = new SecretKeySpec(encodedKey, signatureAlgorithm.getJcaName());
        return signingKey;
	}
	
	public  String createJWT(String subject,long mins) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
			.setId(getId())
			.setIssuer(getId())
			.setIssuedAt(now)
			.setSubject(subject)
		    .signWith(signatureAlgorithm, key);

		long expMillis = nowMillis + mins*1000*60;
		Date exp = new Date(expMillis);
		builder.setExpiration(exp);
		return builder.compact();
	}

	@Override
	public String createNewToken(String context) {
		return createJWT(context, getExpire());
	}



	@Override
	public String parseToken(String token)throws TokenExpiredException, TokenErrorException {
		Claims claims = null;
		try {
			SecretKey key = generalKey();
			claims = Jwts.parser()
					.setSigningKey(key)
					.parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			throw new TokenExpiredException();
		} catch (Exception e) {
			throw new TokenErrorException();
		}
		return claims.getSubject();
	}

	@Override
	public String createNewToken(String context, long mins) {
		return createJWT(context, mins);
	}



}
