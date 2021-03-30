/*
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.bluepower.utility.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.bluepower.narrow_bound.MyAppTask;

@SuppressWarnings("deprecation")
public class HttpsUtil extends DefaultHttpClient 
{
    public final static String HTTPGET = "GET";

    public final static String HTTPPUT = "PUT";

    public final static String HTTPPOST = "POST";

    public final static String HTTPDELETE = "DELETE";

    public final static String HTTPACCEPT = "Accept";

    public final static String CONTENT_LENGTH = "Content-Length";

    public final static String CHARSET_UTF8 = "UTF-8";

	// 缂備緤鎷烽弶鐐村娴兼劕霉閻欏懐鎮奸柣鏍垫嫹 闁荤姴娲ｅ鎺楀礉閻旂儤宕夋い鏍ㄦ皑缁愮偤鏌曢崱鏇熺グ闁伙箑顦扮粙濠囨晝閿熶粙鎯侀幋锔界參闁靛鐓堥崵鐐烘煛瀹ュ牜娼愮�规挷鑳堕敓钘夋贡閸嬨倕顬婇鐐茬畾闁告侗鍘奸弸宀勬煛閸ャ劌顕滅�规洩鎷�
	public static String SELFCERTPATH = "cert//outgoing.CertwithKey.pkcs12";

	public static String SELFCERTPWD = "IoM@1234";

	public static String TRUSTCAPATH = "cert//ca.jks";

	// 闁哄鏅滈悷鈺呭闯閻戣姤鍎嶉柛鏇ㄥ亞濡叉洟鏌ｉ缁橆樂缂佹顦靛浼存偪鐎涘鎮归崶锔筋樂闁告梻鍠栭幆鍐礋椤愩倖顫氶梺娲诲枙缁躲倗妲愬┑瀣殌閻忕偟鍋撶瑧jks闁荤姴娲ｅ鎺楀礉閻斿摜顩烽柟鎯х－濮樸劑鏌ｉ妸銉ヮ仼闁伙附鍨块幆宥夋晸閿燂拷 闂佹寧绋戝﹢鎱夐柣鐘叉矗濡炴帡宕濋悢鐓庡珘妞ゆ帒鍟撮悡鈺佲槈閹惧磭孝閻庡灚锕㈠畷銉╊敃閵堝孩缍勯梻浣割潟閸庤崵妲愬┑瀣倞闁绘ɑ鍓氶崝鍐ㄢ槈閺冨倸鏋戦柣銉ユ嚇瀵灚寰勬繝鍕潥闂佹椿鍠曠欢銈囨閿燂拷
	public static String TRUSTCAPWD = "Huawei@123";
	
	private static HttpClient httpClient;

	/**
	 * 闂佸憡鐟ラ懟顖炲箖濠婂懏濯奸柕鍫濈墢濡插牓鏌涢敂鎯у妺婵炲拑鎷� Two-Way Authentication 
	 * 闂佸憡鐟ラ懟顖炲箖濠婂懏濯奸柕鍫濈墢濡插牓鏌涢敂鎯у妺婵炲懏鐟︾粙澶岋拷锝呭缁�澶愭倵楠炲灝鐏柛鈺傜〒缁晠顢涢悙缈犵帛闁荤喐娲╅幏锟� 
	 * 1闂侀潧妫旂粈渚�顢氶柆宥呯闁靛鍔屽▓鎵拷瑙勭摃缁箖鎯佹径瀣枙闁挎棁鍋愮粈澶愭煙缂佹ê濮冪紒鍓佹暬閹虫盯顢旈崟顓犵暢闁荤姴娲ｅ鎺楀礉閻斿摜鐟规繛鎴炵懄缁犳盯鏌涢弮鎾剁？妞ゅ浚鍓熷浠嬫晸閻橀潧鐭�
	 * 2闂侀潧妫旂粈渚�顢氶柆宥呯闁靛闄勭粻娑㈡煕閺傜尨鎷烽搹顐ゅ綔CA闁荤姴娲ｅ鎺楀礉閻斿吋鏅悘鐐靛帶閳诲繘鏌ｉ埀顒�濡芥繝锟介崶顒�绀夐柨娑樺娴间景A闁荤姴娲ｅ鎺楀礉閻旂厧鍐�闁跨喓濮峰畷锝夋煛閸繄孝濠殿喚鍠撶划鈺咁敍濮橆剛宀涢梻渚囧亗濞村洨鎹㈤崘顔肩骇闁靛／鍕殸闁荤姴娲ｅ鎺楀礉閿燂拷 
	 * 3闂侀潧妫斿ù鍥敊閺囩姷纾炬い鏃囧吹閻熸繈鏌″蹇曠瘈闁绘稒鐟╁畷娲偄闁垮锟斤拷 闂佹寧绋戦悧鎾愁焽椤忓牆鐤柛鈩冪懄閺嗗繘鏌ｅ搴＄仩妞わ絻鍔嶇粙澶岋拷锝呭缁�澶嬬箾鐏炴儳绗氭繝锟芥担瑙勫閻犳亽鍔嶉弳蹇涙煕閳哄啫鏋庨柟顔芥礈閹峰宕稿Δ渚婄椽闂佹寧鍐婚幏锟�
	 * */
	public void initSSLConfigForTwoWay() throws Exception {
		// 1闂侀潧妫旂粈渚�顢氶柆宥呯闁靛鍔屽▓鎵拷瑙勭摃缁箖鎯佹径瀣枙闁跨噦鎷�
		KeyStore selfCert = KeyStore.getInstance("pkcs12");
		try {
			selfCert.load(new FileInputStream(SELFCERTPATH), SELFCERTPWD.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
		kmf.init(selfCert, SELFCERTPWD.toCharArray());

		// 2闂侀潧妫旂粈渚�顢氶柆宥呯闁靛闄勭粻娑㈡煕閺傜尨鎷烽搹顐ゅ綔CA闁荤姴娲ｅ鎺楀礉閿燂拷
		KeyStore caCert = KeyStore.getInstance("jks");
		try {
			caCert.load(new FileInputStream(TRUSTCAPATH), TRUSTCAPWD.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
		tmf.init(caCert);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// 3闂侀潧妫旂粈渚�宕ｈ濮婂顢橀敍鍕潔婵炴垶妫戠粻鎴︽偂濞嗘挸瑙︾�广儱妫涙晶鈥澄旈悪鍛
		// (闂佽壈椴稿Λ渚�鎯冮悢璁垮湱锟斤綆鍘惧Σ鎼佹煟濠婂骸鐏犳い锝冨妽缁嬪顢橀妸褏顦繛鎴炴惈閹风兘鏌ら崜鎻掑闁汇儱鎳樺鍨緞鐏炵偓娈奸柣鐘叉搐閸㈡煡鎮″▎鎾宠Е鐎广儱绻掔粈澶愭煠閺夋寧婀版俊鎻掓啞閹峰懐鎹勯妸锔芥ip闁哄鏅滅粙鏍�侀幋鐘冲闁秆勵殕閿涙牠鏌ｉ妸銉ヮ伒缂佽鲸绻冨璇测枎閹搭厽缍掗梺闈╁閸庛倕鈻嶅▎鎰枖閻庯綆浜炵粻鎴濐渻閵堝牜鍤欓柛娅诲洦鈷掓い鎿勭磿濡插牆鈽夐弮鎾愁洭婵炲牊鍨垮畷娲偄闁垮锟芥娊鏌″蹇曠瘈闁绘稒鐟╁畷婵嬫偄鐠囨彃骞�)
		SSLSocketFactory ssf = new SSLSocketFactory(sc,
				SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		// 婵犵锟藉啿锟藉綊鎮樻径鎰仢妞ゆ牗鍑归弳鏃傦拷瑙勭摃鐏忔瑧鍒掗敓鐘冲仺閻庡湱濯崵鐐裁瑰鍐╊棞闁绘挻鐟╁畷銉ヮ吋韫囨洜顦Δ鐘靛仦濠�鍦箔閺嶃劎鈻旈幖娣�楀Σ鍫濃槈閺冩挾绱板ǎ鍥э躬楠炰線顢涘▎鎺戝箑闂佹眹鍔岀�氼剟鎮″▎鎾宠Е鐎广儱鎳庨悥閬嶆⒑閺夎法鈻岀紒杈ㄧ箞楠炲秴顓奸崨顓☆唹婵炲濮伴崕杈╂閹达箑瑙︽い鏍ㄧ箘濡插牆鈽夐弮鎾剁暠闁绘挻鐟╁畷銉ヮ吋閸℃瑥鈪版俊銈囧閹凤拷 闂佹寧绋戦悧鎾跺垝椤栨粍濯奸柕鍫濆閻﹀秹鏌￠崟闈涚仭濠⒀嶇畱椤曪綁鏁撴禒瀣剭闁告洖澧庣粈锟�
		// SSLSocketFactory ssf = new SSLSocketFactory(sc);

		ClientConnectionManager ccm = this.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 8743, ssf));
		
	    httpClient = new DefaultHttpClient(ccm);
	}

	/**
	 * 闂佸憡顨嗗ú鏍箖濠婂懏濯奸柕鍫濈墢濡插牓鏌涢敂鎯у妺婵炲拑鎷� One-way authentication 
	 * 闂佸憡顨嗗ú鏍箖濠婂懏濯奸柕鍫濈墢濡插牓鏌涢敂鎯у妺婵炲懏鐟︾粙澶岋拷锝呭缁�澶愭倵楠炲灝鐏柛鈺傜〒缁晠顢涢悙缈犵帛闁荤喐娲╅幏锟�
	 * 1闂侀潧妫旂粈渚�顢氶柆宥呯闁靛闄勭粻娑㈡煕閺傜尨鎷烽搹顐ゅ綔CA闁荤姴娲ｅ鎺楀礉閻斿吋鏅悘鐐靛帶閳诲繘鏌ｉ埀顒�濡芥繝锟介崶顒�绀夐柨娑樺娴间景A闁荤姴娲ｅ鎺楀礉閻旂厧鍐�闁跨喓濮峰畷锝夋煛閸繄孝濠殿喚鍠撶划鈺咁敍濮橆剛宀涢梻渚囧亗濞村洨鎹㈤崘顔肩骇闁靛／鍕殸闁荤姴娲ｅ鎺楀礉閿燂拷
	 * 2闂侀潧妫斿ù鍥敊閺囩姷纾炬い鏃囧吹閻熸繈鏌″蹇曠瘈闁绘稒鐟╁畷娲偄闁垮锟斤拷 闂佹寧绋戦悧鎾愁焽椤忓牆鐤柛鈩冪懄閺嗗繘鏌ｅ搴＄仩妞わ絻鍔嶇粙澶岋拷锝呭缁�澶嬬箾鐏炴儳绗氭繝锟芥担瑙勫閻犳亽鍔嶉弳蹇涙煕閳哄啫鏋庨柟顔芥礈閹峰宕稿Δ渚婄椽闂佹寧鍐婚幏锟�
	 * */
	/*
	public void initSSLConfigForOneWay() throws Exception {
		// 闂佺鍩栭幐鍝ワ拷鍨耿閺佸秶浠﹂悙顒婄礆闂婎偄娴勯幏锟�
		System.setProperty("ssl.provider",
				"com.sun.net.ssl.internal.ssl.Provider");
		System.setProperty("ssl.pkgs", "com.sun.net.ssl.internal.www.protocol");
		System.setProperty("javax.net.ssl.keyStore", "D://cert//ca.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "Huawei@123");
		System.setProperty("javax.net.debug", "all");

		// 1闂侀潧妫旂粈渚�顢氶柆宥呯闁靛闄勭粻娑㈡煕閺傜尨鎷烽搹顐ゅ綔CA闁荤姴娲ｅ鎺楀礉閿燂拷
		KeyStore caCert = KeyStore.getInstance("jks");
		caCert.load(new FileInputStream(TRUSTCAPATH), TRUSTCAPWD.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
		tmf.init(caCert);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, tmf.getTrustManagers(), null);

		// 2闂侀潧妫旂粈渚�宕ｈ濮婂顢橀敍鍕潔婵炴垶妫戠粻鎴︽偂濞嗘挸瑙︾�广儱妫涙晶鈥澄旈悪鍛
		// (闂佽壈椴稿Λ渚�鎯冮悢璁垮湱锟斤綆鍘惧Σ鎼佹煟濠婂骸鐏犳い锝冨妽缁嬪顢橀妸褏顦繛鎴炴惈閹风兘鏌ら崜鎻掑闁汇儱鎳樺鍨緞鐏炵偓娈奸柣鐘叉搐閸㈡煡鎮″▎鎾宠Е鐎广儱绻掔粈澶愭煠閺夋寧婀版俊鎻掓啞閹峰懐鎹勯妸锔芥ip闁哄鏅滅粙鏍�侀幋鐘冲闁秆勵殕閿涙牠鏌ｉ妸銉ヮ伒缂佽鲸绻冨璇测枎閹搭厽缍掗梺闈╁閸庛倕鈻嶅▎鎰枖閻庯綆浜炵粻鎴濐渻閵堝牜鍤欓柛娅诲洦鈷掓い鎿勭磿濡插牆鈽夐弮鎾愁洭婵炲牊鍨垮畷娲偄闁垮锟芥娊鏌″蹇曠瘈闁绘稒鐟╁畷婵嬫偄鐠囨彃骞�)
		SSLSocketFactory ssf = new SSLSocketFactory(sc,
				SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		// 婵犵锟藉啿锟藉綊鎮樻径鎰仢妞ゆ牗鍑归弳鏃傦拷瑙勭摃鐏忔瑧鍒掗敓鐘冲仺閻庡湱濯崵鐐裁瑰鍐╊棞闁绘挻鐟╁畷銉ヮ吋韫囨洜顦Δ鐘靛仦濠�鍦箔閺嶃劎鈻旈幖娣�楀Σ鍫濃槈閺冩挾绱板ǎ鍥э躬楠炰線顢涘▎鎺戝箑闂佹眹鍔岀�氼剟鎮″▎鎾宠Е鐎广儱鎳庨悥閬嶆⒑閺夎法鈻岀紒杈ㄧ箞楠炲秴顓奸崨顓☆唹婵炲濮伴崕杈╂閹达箑瑙︽い鏍ㄧ箘濡插牆鈽夐弮鎾剁暠闁绘挻鐟╁畷銉ヮ吋閸℃瑥鈪版俊銈囧閹凤拷 闂佹寧绋戦悧鎾跺垝椤栨粍濯奸柕鍫濆閻﹀秹鏌￠崟闈涚仭濠⒀嶇畱椤曪綁鏁撴禒瀣剭闁告洖澧庣粈锟�
		// SSLSocketFactory ssf = new SSLSocketFactory(sc);

		ClientConnectionManager ccm = this.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 8743, ssf));
		
		httpClient = new DefaultHttpClient(ccm);
	}
*/
    public  HttpResponse doPost(String url, Map<String, String> headerMap,
            StringEntity stringEntity) 
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(stringEntity);

        return executeHttpRequest(request);
    }

    public  HttpResponse doPost(String url, Map<String, String> headerMap,
            InputStream inStream) 
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new InputStreamEntity(inStream));

        return executeHttpRequest(request);
    }

    public  HttpResponse doPostJson(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));

        return executeHttpRequest(request);
    }

    public  String doPostJsonForString(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));
        
        
        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }
        
        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  String doPostJsonForString(String url, String content)
    {
        HttpPost request = new HttpPost(url);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));

        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }
        
        return ((StreamClosedHttpResponse) response).getContent();
    }
    
    private  List<NameValuePair> paramsConverter(Map<String, String> params)
    {
        List<NameValuePair> nvps = new LinkedList<NameValuePair>();
        Set<Map.Entry<String, String>> paramsSet = params.entrySet();
        for (Map.Entry<String, String> paramEntry : paramsSet)
        {
            nvps.add(new BasicNameValuePair(paramEntry.getKey(),
                    	paramEntry.getValue()));
        }

        return nvps;
    }
    
    public String doPostFormUrlEncodedForString(String url, Map<String, String> formParams)
                    throws Exception
    {
        HttpPost request = new HttpPost(url);

        request.setEntity(new UrlEncodedFormEntity(paramsConverter(formParams)));
        MyAppTask.log.debug("request is: " + request);
        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        	 throw new Exception();
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }
    
    public  HttpResponse doPut(String url, Map<String, String> headerMap, InputStream inStream)
    {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new InputStreamEntity(inStream));

        return executeHttpRequest(request);
    }

    public  HttpResponse doPutJson(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));

        return executeHttpRequest(request);
    }
    
    public  String doPutJsonForString(String url,
            Map<String, String> headerMap, String content)
    {
        HttpResponse response = doPutJson(url, headerMap, content);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  HttpResponse doGet(String url, Map<String, String> headerMap)
    {
        HttpGet request = new HttpGet(url);
        addRequestHeader(request, headerMap);

        return executeHttpRequest(request);
    }
    
    public  HttpResponse doGetWithParas(String url, Map<String, String> queryParams, Map<String, String> headerMap)
            throws Exception
    {
        HttpGet request = new HttpGet();
        addRequestHeader(request, headerMap);
        
        URIBuilder builder;
        try
        {
            builder = new URIBuilder(url);
        }
        catch (URISyntaxException e)
        {
            System.out.printf("URISyntaxException: {}", e);
            throw new Exception(e);
            
        }
        
        if (queryParams != null && !queryParams.isEmpty())
        {
            builder.setParameters(paramsConverter(queryParams));
        }
        request.setURI(builder.build());

        return executeHttpRequest(request);
    }
    
    public  String doGetWithParasForString(String url, Map<String, String> mParam, Map<String, String> headerMap)
            throws Exception
    {
        HttpResponse response = doGetWithParas(url, mParam, headerMap);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  HttpResponse doDelete(String url,
            Map<String, String> headerMap)
    {
        HttpDelete request = new HttpDelete(url);
        addRequestHeader(request, headerMap);

        return executeHttpRequest(request);
    }
    
    public  String doDeleteForString(String url,
            Map<String, String> headerMap)
    {
        HttpResponse response = doDelete(url, headerMap);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    private static void addRequestHeader(HttpUriRequest request,
            Map<String, String> headerMap)
    {
        if (headerMap == null)
        {
            return;
        }

        for (String headerName : headerMap.keySet())
        {
            if (CONTENT_LENGTH.equalsIgnoreCase(headerName))
            {
                continue;
            }

            String headerValue = headerMap.get(headerName);
            request.addHeader(headerName, headerValue);
        }
    }

    private  HttpResponse executeHttpRequest(HttpUriRequest request)
    {
        HttpResponse response = null;

        try
        {
            response = httpClient.execute(request);
        }
        catch (Exception e)
        {
            System.out.println("executeHttpRequest failed.");
        }
        finally
        {
            try
            {
                // 闂佸吋鍎抽崲鑼躲亹閸ヮ灐瑙勬媴閻ゎ垰骞�闂佸憡鍔曢幊搴敊閹扮増鏅悘鐐诧拷鐔风彲闂佺绻戞繛濠偽涚�涙ɑ浜ら柣鎰綑婢讹拷
                response = new StreamClosedHttpResponse(response);
            }
            catch (IOException e)
            {
            	 System.out.println("IOException: " + e.getMessage());
            }
        }

        return response;
    }

    public  String getHttpResponseBody(HttpResponse response)
            throws UnsupportedOperationException, IOException
    {
        if (response == null)
        {
            return null;
        }
        
        String body = null;

        if (response instanceof StreamClosedHttpResponse)
        {
            body = ((StreamClosedHttpResponse) response).getContent();
        }
        else
        {
            HttpEntity entity = response.getEntity();
            if (entity != null && entity.isStreaming())
            {
                String encoding = entity.getContentEncoding() != null
                        ? entity.getContentEncoding().getValue() : null;
                body = StreamUtil.inputStream2String(entity.getContent(),
                        encoding);
            }
        }

        return body;
    }
}
