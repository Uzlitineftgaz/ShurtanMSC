package uz.neft.controller.opc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import uz.neft.ecograph.web.xml.Root;
import javafish.clients.opc.browser.JOpcBrowser;
import javafish.clients.opc.exception.*;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.neft.entity.action.CollectionPointAction;
import uz.neft.entity.enums.OpcServerType;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class OpcService {

    enum ValueType{
        PRESSURE,
        TEMPERATURE
    }

    public double getValueWeb(CollectionPointAction collectionPointAction,String unit){
        try {
            if (collectionPointAction.getCollectionPoint().getOpcServer().getType().equals(OpcServerType.REAL)){
                String[] strings = unit.split("\\.");
                RestTemplate restTemplate=new RestTemplate();
                String a=restTemplate.getForObject("http://192.168.5.10/values.xml", String.class);
                XmlMapper xmlMapper = new XmlMapper();
                JsonMapper jsonMapper=new JsonMapper();
                xmlMapper.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
                JSONObject json = XML.toJSONObject(a);
                System.out.println(json);
                Root root=jsonMapper.readValue(json.toString(), Root.class);

                for (int i = 0; i < root.getFieldgate().device.size(); i++) {
                    if (root.getFieldgate().device.get(i).tag.get(0).equals(strings[1])) return root.getFieldgate().device.get(i).v1;
                }

                return 0.0;
            }else {
                return new SecureRandom().nextFloat()*(16.0-13.0)+13.0;
            }

        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }

    public double getValue(CollectionPointAction collectionPointAction,String unit){
        try {
            if (collectionPointAction.getCollectionPoint().getOpcServer().getType().equals(OpcServerType.REAL)){
                try {
                    JOpcBrowser.coInitialize();
                }
                catch (CoInitializeException e1) {
                    e1.printStackTrace();
                }

                // find opc-servers (OpcEnum interface)
                try {
                    String[] opcServers = JOpcBrowser.getOpcServers("localhost");
                    System.out.println(Arrays.asList(opcServers));
                }
                catch (HostException | NotFoundServersException e1) {
                    e1.printStackTrace();
                }

//    JOpcBrowser jbrowser = new JOpcBrowser("localhost", "Matrikon.OPC.Simulation", "JOPCBrowser1");
                JOpcBrowser jbrowser = new JOpcBrowser("localhost", collectionPointAction.getCollectionPoint().getOpcServer().getAddress(),String.valueOf( collectionPointAction.getCollectionPoint().getId()));

                try {
                    jbrowser.connect();
                    String[] branches = jbrowser.getOpcBranch("");
                    System.out.println(Arrays.asList(branches));
                }
                catch (ConnectivityException | UnableBrowseBranchException | UnableIBrowseException e) {
                    e.printStackTrace();
                }

                try {
                    String[] parts = unit.split("\\.");
                    System.out.println(Arrays.toString(parts));
//      String[] items = jbrowser.getOpcItems("Simulation Items.Random", true);

                    String[] items = jbrowser.getOpcItems(parts[0]+"."+parts[1], true);
                    if (items != null) {
                        System.out.println(items.length);
//                    System.out.println(items);
                        for (String item : items) {
                            if (item.contains(parts[0])&&item.contains(parts[2])){
                                String[] itemParts =item.split(";");
                                System.out.println(Arrays.toString(itemParts));
                                if (itemParts.length==4) return Double.parseDouble(itemParts[3].replace(",","."));
                            }
//                        System.out.println(item);
                        }
                    }
                    // disconnect server
                    JOpcBrowser.coUninitialize();
                }
                catch (UnableBrowseLeafException | UnableIBrowseException | UnableAddItemException | CoUninitializeException | UnableAddGroupException e) {
                    e.printStackTrace();
                }
                return 0.0;
            }else {
                return new SecureRandom().nextFloat()*(16.0-13.0)+13.0;
            }

        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }


}
