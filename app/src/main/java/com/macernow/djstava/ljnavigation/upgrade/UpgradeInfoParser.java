package com.macernow.djstava.ljnavigation.upgrade;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by djstava on 15/4/30.
 */
public class UpgradeInfoParser {
    public static UpgradeInfo getUpgradeInfo(InputStream inputStream) throws Exception {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(inputStream,"utf-8");
        int type = xmlPullParser.getEventType();
        UpgradeInfo upgradeInfo = new UpgradeInfo();
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("version".equals(xmlPullParser.getName())) {
                        upgradeInfo.setVersion(xmlPullParser.nextText());
                    } else if ("url".equals(xmlPullParser.getName())) {
                        upgradeInfo.setUrl(xmlPullParser.nextText());
                    } else if ("description".equals(xmlPullParser.getName())) {
                        upgradeInfo.setDescription(xmlPullParser.nextText());
                    }
                    break;
            }

            type = xmlPullParser.next();
        }

        return upgradeInfo;
    }
}
