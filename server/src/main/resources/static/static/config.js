// this file is user config

polyFS_visualizer.default_config = {
   
   // config describing disk stats, set by disk metadata file (disk_meta.js)
   disk_config: {
      user: "Unknown",
      name: "Unknown",
      block_size: 256,
      num_blocks: 10
   },

   // config describing FS -> internal data translation
   data_config: {
      "type": {
         0x01: "superblock", 
         0x02: "inode", 
         0x03: "file-extant", 
         0x04: "free",
         default: "unknown"
      },
      "link": {

      },
      "magic-number": {
         0x44: "valid",
         default: "invalid"
      },
      "empty": {

      }
   },

   // config describing internal data -> display translation
   display_config: {
      
      // block level display options for the entire FS page
      "block": {
         // will set fillstyle based on the value of type with the mappings listed
         "fillstyle": {
            "mapping": {"superblock": '#FFFF00', "inode": '#0000FF', "file-extant": '#000000', "free": '#00FF00'},
            "default": '#FF0000'
         }
      },

      // byte level display options for the block modals
      "byte": {
         "fillstyle": {
            "mapping": {"type": '#FF0000', "magic-number": '#00FF00', "link": '#0000FF', "empty": '#FFFF00'},
            "default": '#000000'
         }
      }
   },
      
   // block configuration 
   block_config: {
      "bytes": [
         "type",
         "magic-number",
         "link",
         "empty"
      ]
   }

}

polyFS_visualizer.config = {

   get_data_config: function(key, value){
      return polyFS_visualizer.config.data_config[key][value] || polyFS_visualizer.config.data_config[key].default;
   },

   get_display_config: function(set, key, value){
      if(polyFS_visualizer.config.display_config[set][key] != null){
         return polyFS_visualizer.config.display_config[set][key].mapping[value] || polyFS_visualizer.config.display_config[set][key].default;
      }
      return null;
   },

   reset: function(){
      polyFS_visualizer.config.disk_config = polyFS_visualizer.default_config.disk_config;
      polyFS_visualizer.config.data_config = polyFS_visualizer.default_config.data_config;
      polyFS_visualizer.config.display_config = polyFS_visualizer.default_config.display_config;
      polyFS_visualizer.config.block_config = polyFS_visualizer.default_config.block_config;
   }

}

polyFS_visualizer.config.reset();
