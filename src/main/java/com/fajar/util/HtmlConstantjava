using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.IO;
using System.Reflection;
using OrgWebMvc.Main.TxtResources;

namespace OrgWebMvc.Main.Util
{
    public class HtmlConstant
    {
        public static string ReadRftFile()
        {
            
            string currentDir = Path.GetDirectoryName(typeof(MainDir).Assembly.Location);

            string RFT = File.ReadAllText(currentDir+"/Rtf.txt");
            return RFT;
        }

        public static string RTFTemplate = "<div><!--INIT FORM--><div id=\"toolBar1\">"+
            "<select onchange=\"formatDoc('formatblock',this[this.selectedIndex].value);this.selectedIndex=0;\">"+
            "<option selected>- formatting -</option>            <option value=\"h1\">Title 1 &lt;h1&gt;</option>            <option value=\"h2\">Title 2 &lt;h2&gt;</option>            <option value=\"h3\">Title 3 &lt;h3&gt;</option>            <option value=\"h4\">Title 4 &lt;h4&gt;</option>            <option value=\"h5\">Title 5 &lt;h5&gt;</option>            <option value=\"h6\">Subtitle &lt;h6&gt;</option>            <option value=\"p\">Paragraph &lt;p&gt;</option>            <option value=\"pre\">Preformatted &lt;pre&gt;</option>        </select>"+
            "<select onchange=\"formatDoc('fontname',this[this.selectedIndex].value);this.selectedIndex=0;\">            <option class=\"heading\" selected>- font -</option>            <option>Arial</option>            <option>Arial Black</option>            <option>Courier New</option>            <option>Times New Roman</option>        </select>"+
            "<select onchange=\"formatDoc('fontsize',this[this.selectedIndex].value);this.selectedIndex=0;\">            <option class=\"heading\" selected>- size -</option>            <option value=\"1\">Very small</option>            <option value=\"2\">A bit small</option>            <option value=\"3\">Normal</option>            <option value=\"4\">Medium-large</option>            <option value=\"5\">Big</option>            <option value=\"6\">Very big</option>            <option value=\"7\">Maximum</option>        </select>"+
            "<select onchange=\"formatDoc('forecolor',this[this.selectedIndex].value);this.selectedIndex=0;\">            <option class=\"heading\" selected>- color -</option>            <option value=\"red\">Red</option>            <option value=\"blue\">Blue</option>            <option value=\"green\">Green</option>            <option value=\"black\">Black</option>        </select>"+
            "<select onchange=\"formatDoc('backcolor',this[this.selectedIndex].value);this.selectedIndex=0;\">            <option class=\"heading\" selected>- background -</option>            <option value=\"red\">Red</option>            <option value=\"green\">Green</option>            <option value=\"black\">Black</option>        </select>    </div>"+
            "<div id=\"toolBar2\">"+
            "<img class=\"intLink\" title=\"Clean\" onclick=\"if(validateMode()&&confirm('Are you sure?')){oDoc.innerHTML=sDefTxt};\" src=\"../images/clean.gif\" /> "+
            "<button class=\"intLink\" title=\"Print\" onclick=\"printDoc();\"><span class=\"glyphicon glyphicon-print\"></span></button>" +
            "<button class=\"intLink\" title=\"Undo\" onclick=\"formatDoc('undo');\" >&#8634;</button>" +
            "<button class=\"intLink\" title=\"Redo\" onclick=\"formatDoc('redo');\" >&#8635;</button>" +
            "<img class=\"intLink\" title=\"Remove formatting\" onclick=\"formatDoc('removeFormat')\" src=\"../images/formating.png\">" +
            "<button  title=\"Bold\" onclick=\"formatDoc('bold');\"><span class=\"glyphicon glyphicon-bold\"/></button>" +
            "<button  title=\"Italic\" onclick=\"formatDoc('italic');\"><span class=\"glyphicon glyphicon-italic\" /></button>" +
            "<button class=\"intLink\" title=\"Underline\" onclick=\"formatDoc('underline');\" >&#9089;</button>" +
            "<button title=\"Left align\" onclick=\"formatDoc('justifyleft');\" ><span class=\"glyphicon glyphicon-align-left\" /> </button>" +
            "<button title=\"Center align\" onclick=\"formatDoc('justifycenter');\" > <span class=\"glyphicon glyphicon-align-center\" /></button>" +
            "<button  title=\"Right align\" onclick=\"formatDoc('justifyright');\"  ><span class=\"glyphicon glyphicon-align-right\"/></button>" +
            "<img class=\"intLink\" title=\"Numbered list\" onclick=\"formatDoc('insertorderedlist');\" src=\"../images/number.gif\" />" +
            "<img class=\"intLink\" title=\"Dotted list\" onclick=\"formatDoc('insertunorderedlist');\" src=\"../images/bullet.gif\" />" +
            "<button class=\"intLink\" title=\"Quote\" onclick=\"formatDoc('formatblock','blockquote');\" >&#10077;</button>" +
            "<button title=\"Add indentation\" onclick=\"formatDoc('outdent');\" ><span class=\"glyphicon glyphicon-indent-right\" /></button>" +
            "<button  title=\"Delete indentation\" onclick=\"formatDoc('indent');\"><span class=\"glyphicon glyphicon-indent-left\" /></button>" +
            "<button class=\"intLink\" title=\"Hyperlink\" onclick=\"var sLnk=prompt('Write the URL here','http:\\/\\/');if(sLnk&&sLnk!=''&&sLnk!='http://'){formatDoc('createlink',sLnk)}\" >&#128279;</button>" +
            "<button class=\"intLink\" title=\"Cut\" onclick=\"formatDoc('cut');\" >&#9986;</button>"+
            "<img class=\"intLink\" title=\"Copy\" onclick=\"formatDoc('copy');\" src=\"../images/copy.gif\" />" +
            "<img class=\"intLink\" title=\"Paste\" onclick=\"formatDoc('paste');\" src=\"../images/paste.gif\" />" +
            "<button class=\"intLink\" title=\"Insert Image\" onclick=\"Confirm.render()\" src=\"../images/img.png\" height=\"17\" >&#127924;</button>   </div>"
            + "<div id=\"${INPUTID}\" class=\"textBox-rtf\" name=\"input-entity\" style=\"height:300px;width:100%;border:1px solid;overflow: scroll\" contenteditable=\"true\" role=\"rtf\">${VALUE}</div>    <p id=\"editMode\"><input type=\"checkbox\" name=\"switchMode\" id=\"switchBox\" onchange=\"setDocMode(this.checked);\" /> <label for=\"switchBox\">Show HTML</label></p>    <!--END FORM--></div>";
    }
}