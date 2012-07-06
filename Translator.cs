using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Whitehole
{
    public static class Translator
    {
        // inspired from: http://stackoverflow.com/questions/2246017/c-sharp-google-translate
        public static string Translate(string input, string languagePair)
        {
            string url = String.Format("http://www.google.com/translate_t?hl=en&ie=UTF8&text={0}&langpair={1}", input, languagePair);
            WebClient webClient = new WebClient();
            webClient.Encoding = System.Text.Encoding.UTF8;
            string result = webClient.DownloadString(url);
            string final = "";

            string sep = "<span id=result_box class=\"short_text\">";
            result = result.Substring(result.IndexOf(sep) + sep.Length);

            sep = "<span title=\"";
            while (result.Contains(sep))
            {
                result = result.Substring(result.IndexOf(sep) + sep.Length);
                result = result.Substring(result.IndexOf('>') + 1);

                final += result.Substring(0, result.IndexOf("</span>"));
                result = result.Substring(result.IndexOf("</span>") + "</span>".Length);
            }

            return final;
        }

    }
}
