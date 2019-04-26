package com.blobcity.utils;

import android.os.Environment;

import java.util.UUID;

public class ConstantPath {

    public static String FOLDER_NAME = "folder_name";
    public static String DYNAMIC_PATH = "dynamic_path";
    public static String WEBVIEW_PATH = "file:///android_asset/";
    public static String UNIQUE_KEY_LIST= "key_list";
    public static String ARRAY_MAP_LIST = "map_list";
    public static String COURSE_ID = "course_id";
    public static String TOPIC_ID = "topic_id";
    public static String TOPIC_LEVEL = "topic_level";
    public static String LEVEL_COMPLETED = "level_completed";
    public static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    /*assetPath*/

    public static String assetOutputPath = "output/";
    public static String assetTestCoursePath = assetOutputPath +"Test Course/";

    public static String assetTopicOnePath = assetTestCoursePath+"topic-one/";
    public static String assetBasic394Path = assetTopicOnePath +"basic-394";
    public static String assetBasic395Path = assetTopicOnePath +"basic-395";
    public static String assetBasic396Path = assetTopicOnePath +"basic-396";
    public static String assetBasic397Path = assetTopicOnePath +"basic-397";
    public static String assetBasic408Path = assetTopicOnePath +"basic-408";
    public static String assetBasic409Path = assetTopicOnePath +"basic-409";
    public static String assetBasic550Path = assetTopicOnePath +"basic-550";
    public static String assetBasic1421Path = assetTopicOnePath +"basic-1421";
    public static String assetBasic1422Path = assetTopicOnePath +"basic-1422";
    public static String assetAdvanced398Path = assetTopicOnePath +"advanced-398";
    public static String assetAdvanced399Path = assetTopicOnePath +"advanced-399";
    public static String assetAdvanced403Path = assetTopicOnePath +"advanced-403";
    public static String assetAdvanced404Path = assetTopicOnePath +"advanced-404";
    public static String assetAdvanced1429Path = assetTopicOnePath +"advanced-1429";
    public static String assetAdvanced1430Path = assetTopicOnePath +"advanced-1430";
    public static String assetAdvanced1431Path = assetTopicOnePath +"advanced-1431";
    public static String assetAdvanced1432Path = assetTopicOnePath +"advanced-1432";
    public static String assetIntermediate400Path = assetTopicOnePath +"intermediate-400";
    public static String assetIntermediate401Path = assetTopicOnePath +"intermediate-401";
    public static String assetIntermediate402Path = assetTopicOnePath +"intermediate-402";
    public static String assetIntermediate405Path = assetTopicOnePath +"intermediate-405";
    public static String assetIntermediate413Path = assetTopicOnePath +"intermediate-413";
    public static String assetIntermediate549Path = assetTopicOnePath +"intermediate-549";
    public static String assetIntermediate1426Path = assetTopicOnePath +"intermediate-1426";
    public static String assetIntermediate1427Path = assetTopicOnePath +"intermediate-1427";
    public static String assetIntermediate1428Path = assetTopicOnePath +"intermediate-1428";

    public static String assetTopicTwoPath = assetTestCoursePath+"topic-two/";
    public static String assetBasic406Path = assetTopicTwoPath +"basic-406";
    public static String assetBasic407Path = assetTopicTwoPath +"basic-407";
    public static String assetBasic1423Path = assetTopicTwoPath +"basic-1423";
    public static String assetBasic1424Path = assetTopicTwoPath +"basic-1424";
    public static String assetAdvanced410Path = assetTopicTwoPath +"advanced-410";
    public static String assetAdvanced411Path = assetTopicTwoPath +"advanced-411";
    public static String assetAdvanced415Path = assetTopicTwoPath +"advanced-415";
    public static String assetAdvanced416Path = assetTopicTwoPath +"advanced-416";
    public static String assetIntermediate412Path = assetTopicTwoPath +"intermediate-412";
    public static String assetIntermediate414Path = assetTopicTwoPath +"intermediate-414";
    public static String assetIntermediate417Path = assetTopicTwoPath +"intermediate-417";
    public static String assetIntermediate1425Path = assetTopicTwoPath +"intermediate-1425";

    public static String assetTopicThreePath = assetTestCoursePath+"topic-three/";
    public static String assetBasic418Path = assetTopicThreePath +"basic-418";
    public static String assetBasic419Path = assetTopicThreePath +"basic-419";
    public static String assetBasic420Path = assetTopicThreePath +"basic-420";
    public static String assetBasic421Path = assetTopicThreePath +"basic-421";
    public static String assetAdvanced422Path = assetTopicThreePath +"advanced-422";
    public static String assetAdvanced423Path = assetTopicThreePath +"advanced-423";
    public static String assetAdvanced427Path = assetTopicThreePath +"advanced-427";
    public static String assetAdvanced428Path = assetTopicThreePath +"advanced-428";
    public static String assetIntermediate424Path = assetTopicThreePath +"intermediate-424";
    public static String assetIntermediate425Path = assetTopicThreePath +"intermediate-425";
    public static String assetIntermediate426Path = assetTopicThreePath +"intermediate-426";
    public static String assetIntermediate429Path = assetTopicThreePath +"intermediate-429";

    public static String assetTopicFourPath = assetTestCoursePath+"topic-four/";
    public static String assetBasic430Path = assetTopicFourPath +"basic-430";
    public static String assetBasic431Path = assetTopicFourPath +"basic-431";
    public static String assetBasic432Path = assetTopicFourPath +"basic-432";
    public static String assetBasic433Path = assetTopicFourPath +"basic-433";
    public static String assetAdvanced434Path = assetTopicFourPath +"advanced-434";
    public static String assetAdvanced435Path = assetTopicFourPath +"advanced-435";
    public static String assetAdvanced439Path = assetTopicFourPath +"advanced-439";
    public static String assetAdvanced440Path = assetTopicFourPath +"advanced-440";
    public static String assetIntermediate436Path = assetTopicFourPath +"intermediate-436";
    public static String assetIntermediate437Path = assetTopicFourPath +"intermediate-437";
    public static String assetIntermediate438Path = assetTopicFourPath +"intermediate-438";
    public static String assetIntermediate441Path = assetTopicFourPath +"intermediate-441";

    public static String assetTopicFivePath = assetTestCoursePath+"topic-five/";
    public static String assetBasic442Path = assetTopicFivePath +"basic-442";
    public static String assetBasic443Path = assetTopicFivePath +"basic-443";
    public static String assetBasic444Path = assetTopicFivePath +"basic-444";
    public static String assetBasic445Path = assetTopicFivePath +"basic-445";
    public static String assetAdvanced446Path = assetTopicFivePath +"advanced-446";
    public static String assetAdvanced447Path = assetTopicFivePath +"advanced-447";
    public static String assetAdvanced451Path = assetTopicFivePath +"advanced-451";
    public static String assetAdvanced452Path = assetTopicFivePath +"advanced-452";
    public static String assetIntermediate448Path = assetTopicFivePath +"intermediate-448";
    public static String assetIntermediate449Path = assetTopicFivePath +"intermediate-449";
    public static String assetIntermediate450Path = assetTopicFivePath +"intermediate-450";
    public static String assetIntermediate453Path = assetTopicFivePath +"intermediate-453";

    public static String assetTopicSixPath = assetTestCoursePath+"topic-six/";
    public static String assetBasic454Path = assetTopicSixPath +"basic-454";
    public static String assetBasic455Path = assetTopicSixPath +"basic-455";
    public static String assetBasic456Path = assetTopicSixPath +"basic-456";
    public static String assetBasic457Path = assetTopicSixPath +"basic-457";
    public static String assetAdvanced458Path = assetTopicSixPath +"advanced-458";
    public static String assetAdvanced459Path = assetTopicSixPath +"advanced-459";
    public static String assetAdvanced463Path = assetTopicSixPath +"advanced-463";
    public static String assetAdvanced464Path = assetTopicSixPath +"advanced-464";
    public static String assetIntermediate460Path = assetTopicSixPath +"intermediate-460";
    public static String assetIntermediate461Path = assetTopicSixPath +"intermediate-461";
    public static String assetIntermediate462Path = assetTopicSixPath +"intermediate-462";
    public static String assetIntermediate465Path = assetTopicSixPath +"intermediate-465";

    public static String assetTopicSevenPath = assetTestCoursePath+"topic-seven/";
    public static String assetBasic466Path = assetTopicSevenPath +"basic-466";
    public static String assetBasic467Path = assetTopicSevenPath +"basic-467";
    public static String assetBasic468Path = assetTopicSevenPath +"basic-468";
    public static String assetBasic469Path = assetTopicSevenPath +"basic-469";
    public static String assetAdvanced470Path = assetTopicSevenPath +"advanced-470";
    public static String assetAdvanced471Path = assetTopicSevenPath +"advanced-471";
    public static String assetAdvanced475Path = assetTopicSevenPath +"advanced-475";
    public static String assetAdvanced476Path = assetTopicSevenPath +"advanced-476";
    public static String assetIntermediate472Path = assetTopicSevenPath +"intermediate-472";
    public static String assetIntermediate473Path = assetTopicSevenPath +"intermediate-473";
    public static String assetIntermediate474Path = assetTopicSevenPath +"intermediate-474";
    public static String assetIntermediate477Path = assetTopicSevenPath +"intermediate-477";

    public static String assetTopicEightPath = assetTestCoursePath+"topic-eight/";
    public static String assetBasic478Path = assetTopicEightPath +"basic-478";
    public static String assetBasic479Path = assetTopicEightPath +"basic-479";
    public static String assetBasic480Path = assetTopicEightPath +"basic-480";
    public static String assetBasic481Path = assetTopicEightPath +"basic-481";
    public static String assetAdvanced482Path = assetTopicEightPath +"advanced-482";
    public static String assetAdvanced483Path = assetTopicEightPath +"advanced-483";
    public static String assetAdvanced487Path = assetTopicEightPath +"advanced-487";
    public static String assetAdvanced488Path = assetTopicEightPath +"advanced-488";
    public static String assetIntermediate484Path = assetTopicEightPath +"intermediate-484";
    public static String assetIntermediate485Path = assetTopicEightPath +"intermediate-485";
    public static String assetIntermediate486Path = assetTopicEightPath +"intermediate-486";
    public static String assetIntermediate489Path = assetTopicEightPath +"intermediate-489";

    public static String assetTopicNinePath = assetTestCoursePath+"topic-nine/";
    public static String assetBasic490Path = assetTopicNinePath +"basic-490";
    public static String assetBasic491Path = assetTopicNinePath +"basic-491";
    public static String assetBasic492Path = assetTopicNinePath +"basic-492";
    public static String assetBasic493Path = assetTopicNinePath +"basic-493";
    public static String assetAdvanced494Path = assetTopicNinePath +"advanced-494";
    public static String assetAdvanced495Path = assetTopicNinePath +"advanced-495";
    public static String assetAdvanced499Path = assetTopicNinePath +"advanced-499";
    public static String assetAdvanced500Path = assetTopicNinePath +"advanced-500";
    public static String assetIntermediate496Path = assetTopicNinePath +"intermediate-496";
    public static String assetIntermediate497Path = assetTopicNinePath +"intermediate-497";
    public static String assetIntermediate498Path = assetTopicNinePath +"intermediate-498";
    public static String assetIntermediate501Path = assetTopicNinePath +"intermediate-501";

    public static String assetTopicTenPath = assetTestCoursePath+"topic-ten/";
    public static String assetBasic502Path = assetTopicTenPath +"basic-502";
    public static String assetBasic503Path = assetTopicTenPath +"basic-503";
    public static String assetBasic504Path = assetTopicTenPath +"basic-504";
    public static String assetBasic505Path = assetTopicTenPath +"basic-505";
    public static String assetAdvanced506Path = assetTopicTenPath +"advanced-506";
    public static String assetAdvanced507Path = assetTopicTenPath +"advanced-507";
    public static String assetAdvanced511Path = assetTopicTenPath +"advanced-511";
    public static String assetAdvanced512Path = assetTopicTenPath +"advanced-512";
    public static String assetIntermediate508Path = assetTopicTenPath +"intermediate-508";
    public static String assetIntermediate509Path = assetTopicTenPath +"intermediate-509";
    public static String assetIntermediate510Path = assetTopicTenPath +"intermediate-510";
    public static String assetIntermediate513Path = assetTopicTenPath +"intermediate-513";


                                    /**localPath**/

    public static String localPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String localOutputPath = localPath +"/blobcity";
    public static String localTestCoursePath = localOutputPath +"/Test Course";

    public static String localTopicOnePath = localTestCoursePath +"/topic-one";
    public static String localBasic394Path = localTopicOnePath +"/basic-394/";
    public static String localBasic395Path = localTopicOnePath +"/basic-395/";
    public static String localBasic396Path = localTopicOnePath +"/basic-396/";
    public static String localBasic397Path = localTopicOnePath +"/basic-397/";
    public static String localBasic408Path = localTopicOnePath +"/basic-408/";
    public static String localBasic409Path = localTopicOnePath +"/basic-409/";
    public static String localBasic550Path = localTopicOnePath +"/basic-550/";
    public static String localBasic1421Path = localTopicOnePath +"/basic-1421/";
    public static String localBasic1422Path = localTopicOnePath +"/basic-1422/";
    public static String localAdvanced398Path = localTopicOnePath +"/advanced-398/";
    public static String localAdvanced399Path = localTopicOnePath +"/advanced-399/";
    public static String localAdvanced403Path = localTopicOnePath +"/advanced-403/";
    public static String localAdvanced404Path = localTopicOnePath +"/advanced-404/";
    public static String localAdvanced1429Path = localTopicOnePath +"/advanced-1429/";
    public static String localAdvanced1430Path = localTopicOnePath +"/advanced-1430/";
    public static String localAdvanced1431Path = localTopicOnePath +"/advanced-1431/";
    public static String localAdvanced1432Path = localTopicOnePath +"/advanced-1432/";
    public static String localIntermediate400Path = localTopicOnePath +"/intermediate-400/";
    public static String localIntermediate401Path = localTopicOnePath +"/intermediate-401/";
    public static String localIntermediate402Path = localTopicOnePath +"/intermediate-402/";
    public static String localIntermediate405Path = localTopicOnePath +"/intermediate-405/";
    public static String localIntermediate413Path = localTopicOnePath +"/intermediate-413/";
    public static String localIntermediate549Path = localTopicOnePath +"/intermediate-549/";
    public static String localIntermediate1426Path = localTopicOnePath +"/intermediate-1426/";
    public static String localIntermediate1427Path = localTopicOnePath +"/intermediate-1427/";
    public static String localIntermediate1428Path = localTopicOnePath +"/intermediate-1428/";

    public static String localTopicTwoPath = localTestCoursePath +"/topic-two";
    public static String localBasic406Path = localTopicTwoPath +"/basic-406/";
    public static String localBasic407Path = localTopicTwoPath +"/basic-407/";
    public static String localBasic1423Path = localTopicTwoPath +"/basic-1423/";
    public static String localBasic1424Path = localTopicTwoPath +"/basic-1424/";
    public static String localAdvanced410Path = localTopicTwoPath +"/advanced-410/";
    public static String localAdvanced411Path = localTopicTwoPath +"/advanced-411/";
    public static String localAdvanced415Path = localTopicTwoPath +"/advanced-415/";
    public static String localAdvanced416Path = localTopicTwoPath +"/advanced-416/";
    public static String localIntermediate412Path = localTopicTwoPath +"/intermediate-412/";
    public static String localIntermediate414Path = localTopicTwoPath +"/intermediate-414/";
    public static String localIntermediate417Path = localTopicTwoPath +"/intermediate-417/";
    public static String localIntermediate1425Path = localTopicTwoPath +"/intermediate-1425/";

    public static String localTopicThreePath = localTestCoursePath +"/topic-three";
    public static String localBasic418Path = localTopicThreePath +"/basic-418/";
    public static String localBasic419Path = localTopicThreePath +"/basic-419/";
    public static String localBasic420Path = localTopicThreePath +"/basic-420/";
    public static String localBasic421Path = localTopicThreePath +"/basic-421/";
    public static String localAdvanced422Path = localTopicThreePath +"/advanced-422/";
    public static String localAdvanced423Path = localTopicThreePath +"/advanced-423/";
    public static String localAdvanced427Path = localTopicThreePath +"/advanced-427/";
    public static String localAdvanced428Path = localTopicThreePath +"/advanced-428/";
    public static String localIntermediate424Path = localTopicThreePath +"/intermediate-424/";
    public static String localIntermediate425Path = localTopicThreePath +"/intermediate-424/";
    public static String localIntermediate426Path = localTopicThreePath +"/intermediate-426/";
    public static String localIntermediate429Path = localTopicThreePath +"/intermediate-429/";

    public static String localTopicFourPath = localTestCoursePath +"/topic-four";
    public static String localBasic430Path = localTopicFourPath +"/basic-430/";
    public static String localBasic431Path = localTopicFourPath +"/basic-431/";
    public static String localBasic432Path = localTopicFourPath +"/basic-432/";
    public static String localBasic433Path = localTopicFourPath +"/basic-433/";
    public static String localAdvanced434Path = localTopicFourPath +"/advanced-434/";
    public static String localAdvanced435Path = localTopicFourPath +"/advanced-435/";
    public static String localAdvanced439Path = localTopicFourPath +"/advanced-439/";
    public static String localAdvanced440Path = localTopicFourPath +"/advanced-440/";
    public static String localIntermediate436Path = localTopicFourPath +"/intermediate-436/";
    public static String localIntermediate437Path = localTopicFourPath +"/intermediate-437/";
    public static String localIntermediate438Path = localTopicFourPath +"/intermediate-438/";
    public static String localIntermediate441Path = localTopicFourPath +"/intermediate-441/";

    public static String localTopicFivePath = localTestCoursePath +"/topic-five";
    public static String localBasic442Path = localTopicFivePath +"/basic-442/";
    public static String localBasic443Path = localTopicFivePath +"/basic-443/";
    public static String localBasic444Path = localTopicFivePath +"/basic-444/";
    public static String localBasic445Path = localTopicFivePath +"/basic-445/";
    public static String localAdvanced446Path = localTopicFivePath +"/advanced-446/";
    public static String localAdvanced447Path = localTopicFivePath +"/advanced-447/";
    public static String localAdvanced451Path = localTopicFivePath +"/advanced-451/";
    public static String localAdvanced452Path = localTopicFivePath +"/advanced-452/";
    public static String localIntermediate448Path = localTopicFivePath +"/intermediate-448/";
    public static String localIntermediate449Path = localTopicFivePath +"/intermediate-449/";
    public static String localIntermediate450Path = localTopicFivePath +"/intermediate-450/";
    public static String localIntermediate453Path = localTopicFivePath +"/intermediate-453/";

    public static String localTopicSixPath = localTestCoursePath +"/topic-six";
    public static String localBasic454Path = localTopicSixPath +"/basic-454/";
    public static String localBasic455Path = localTopicSixPath +"/basic-455/";
    public static String localBasic456Path = localTopicSixPath +"/basic-456/";
    public static String localBasic457Path = localTopicSixPath +"/basic-457/";
    public static String localAdvanced458Path = localTopicSixPath +"/advanced-458/";
    public static String localAdvanced459Path = localTopicSixPath +"/advanced-459/";
    public static String localAdvanced463Path = localTopicSixPath +"/advanced-463/";
    public static String localAdvanced464Path = localTopicSixPath +"/advanced-464/";
    public static String localIntermediate460Path = localTopicSixPath +"/intermediate-460/";
    public static String localIntermediate461Path = localTopicSixPath +"/intermediate-461/";
    public static String localIntermediate462Path = localTopicSixPath +"/intermediate-462/";
    public static String localIntermediate465Path = localTopicSixPath +"/intermediate-465/";

    public static String localTopicSevenPath = localTestCoursePath +"/topic-seven";
    public static String localBasic466Path = localTopicSevenPath +"/basic-466/";
    public static String localBasic467Path = localTopicSevenPath +"/basic-467/";
    public static String localBasic468Path = localTopicSevenPath +"/basic-468/";
    public static String localBasic469Path = localTopicSevenPath +"/basic-469/";
    public static String localAdvanced470Path = localTopicSevenPath +"/advanced-470/";
    public static String localAdvanced471Path = localTopicSevenPath +"/advanced-471/";
    public static String localAdvanced475Path = localTopicSevenPath +"/advanced-475/";
    public static String localAdvanced476Path = localTopicSevenPath +"/advanced-476/";
    public static String localIntermediate472Path = localTopicSevenPath +"/intermediate-472/";
    public static String localIntermediate473Path = localTopicSevenPath +"/intermediate-473/";
    public static String localIntermediate474Path = localTopicSevenPath +"/intermediate-474/";
    public static String localIntermediate477Path = localTopicSevenPath +"/intermediate-477/";

    public static String localTopicEightPath = localTestCoursePath +"/topic-eight";
    public static String localBasic478Path = localTopicEightPath +"/basic-478/";
    public static String localBasic479Path = localTopicEightPath +"/basic-479/";
    public static String localBasic480Path = localTopicEightPath +"/basic-480/";
    public static String localBasic481Path = localTopicEightPath +"/basic-481/";
    public static String localAdvanced482Path = localTopicEightPath +"/advanced-482/";
    public static String localAdvanced483Path = localTopicEightPath +"/advanced-483/";
    public static String localAdvanced487Path = localTopicEightPath +"/advanced-487/";
    public static String localAdvanced488Path = localTopicEightPath +"/advanced-488/";
    public static String localIntermediate484Path = localTopicEightPath +"/intermediate-484/";
    public static String localIntermediate485Path = localTopicEightPath +"/intermediate-485/";
    public static String localIntermediate486Path = localTopicEightPath +"/intermediate-486/";
    public static String localIntermediate489Path = localTopicEightPath +"/intermediate-489/";

    public static String localTopicNinePath = localTestCoursePath +"/topic-nine";
    public static String localBasic490Path = localTopicNinePath +"/basic-490/";
    public static String localBasic491Path = localTopicNinePath +"/basic-491/";
    public static String localBasic492Path = localTopicNinePath +"/basic-492/";
    public static String localBasic493Path = localTopicNinePath +"/basic-493/";
    public static String localAdvanced494Path = localTopicNinePath +"/advanced-494/";
    public static String localAdvanced495Path = localTopicNinePath +"/advanced-495/";
    public static String localAdvanced499Path = localTopicNinePath +"/advanced-499/";
    public static String localAdvanced500Path = localTopicNinePath +"/advanced-500/";
    public static String localIntermediate496Path = localTopicNinePath +"/intermediate-496/";
    public static String localIntermediate497Path = localTopicNinePath +"/intermediate-497/";
    public static String localIntermediate498Path = localTopicNinePath +"/intermediate-498/";
    public static String localIntermediate501Path = localTopicNinePath +"/intermediate-501/";

    public static String localTopicTenPath = localTestCoursePath +"/topic-ten";
    public static String localBasic502Path = localTopicTenPath +"/basic-502/";
    public static String localBasic503Path = localTopicTenPath +"/basic-503/";
    public static String localBasic504Path = localTopicTenPath +"/basic-504/";
    public static String localBasic505Path = localTopicTenPath +"/basic-505/";
    public static String localAdvanced506Path = localTopicTenPath +"/advanced-506/";
    public static String localAdvanced507Path = localTopicTenPath +"/advanced-507/";
    public static String localAdvanced511Path = localTopicTenPath +"/advanced-511/";
    public static String localAdvanced512Path = localTopicTenPath +"/advanced-512/";
    public static String localIntermediate508Path = localTopicTenPath +"/intermediate-508/";
    public static String localIntermediate509Path = localTopicTenPath +"/intermediate-509/";
    public static String localIntermediate510Path = localTopicTenPath +"/intermediate-510/";
    public static String localIntermediate513Path = localTopicTenPath +"/intermediate-513/";
}
