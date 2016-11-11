

polyFS_visualizer.disk_config = {

   load: function (disk) {

      polyFS_visualizer.config.reset();
      console.log("loading config data: " + disk);
      $.ajax({
         type: "GET",
         dataType: 'binary',
         processData: false,
         responseType:'text',
         url: "disks/" + disk + ".config",
      })
      .done(function(result) {
         console.log("found disk config");
         polyFS_visualizer.disk_config.update(result);
      })
      .fail(function() {
         console.log("could not find disk config");
         warning( "disk config does not exist" );
      })
   },

   verify_config: function(user_config){

      var dummy_config = {
      
         data_config: {
            "type": {
               default: "unknown"
            },
            "magic-number": {
               default: "invalid"
            },
         },

         display_config: {
            
            "block": {
               "fillstyle": {
                  "mapping": {},
                  "default": ''
               }
            },

            "byte": {
               "fillstyle": {
                  "mapping": {},
                  "default": ''
               }
            }
         },
            
         block_config: {
            "bytes": [
            ]
         }

      }
      
      if(!user_config){
         warning("user config is empty");
         return false;
      }

      // make sure the json matches the dummy config
      function recurseCheckConfig(dummy_config, user_config, name)
      {
         for (var k in dummy_config) {

            // check for missing key
            if (!(k in user_config)) {
               warning("config failed to load: " + k + " missing from " + name);
               return false;
            }
            
            // check for wrong type
            if (typeof dummy_config[k] != typeof user_config[k]) {
               warning("config failed to load: " + k + " in " + name + " must be type " + typeof dummy_config[k]);
               return false;
            }

            // check sub parts
            if (typeof dummy_config[k] == "object" && dummy_config[k] !== null) {
               if (!recurseCheckConfig(dummy_config[k], user_config[k], k)) {
                  return false;
               }
            }
         }

         return true;
      }

      // check all base config blocks
      if ("data_config" in user_config) {
         if(!recurseCheckConfig(dummy_config.data_config, user_config.data_config, "data_config")){
            return false;
         }
      }
      if ("display_config" in user_config) {
         if(!recurseCheckConfig(dummy_config.display_config, user_config.display_config, "display_config")){
            return false;
         }
      }
      if ("block_config" in user_config) {
         if(!recurseCheckConfig(dummy_config.block_config, user_config.block_config, "block_config")){
            return false;
         }
      }

      return true;
   },

   update: function (config_string){

      // parse the json
      try{
         user_config = JSON.parse(config_string);
      }
      catch(e){
         warning("config failed to load: " + e.message);
         return;
      }

      // verify the config format
      if (!polyFS_visualizer.disk_config.verify_config(user_config)){
         return;
      }
      
      // update the config
      console.log("updating to user config");

      if ("data_config" in user_config) {
         console.log("replacing data config");

         // all hex values must be converted from string to int
         replacement = {}
         for (meta_type in user_config.data_config){
            replacement[meta_type] = {}
            for (value in user_config.data_config[meta_type]) {
               if (value != "default") {
                  num = parseInt(value, 16);
                  replacement[meta_type][num] = user_config.data_config[meta_type][value];
               }
            }
         }
         user_config.data_config = replacement

         polyFS_visualizer.config.data_config = user_config.data_config;

      }
      if ("display_config" in user_config) {
         console.log("replacing display config");
         polyFS_visualizer.config.display_config = user_config.display_config;
      }
      if ("block_config" in user_config) {
         console.log("replacing block config");
         polyFS_visualizer.config.block_config = user_config.block_config;
      }

      polyFS_visualizer.load_disk.reload_page();
   }

}
