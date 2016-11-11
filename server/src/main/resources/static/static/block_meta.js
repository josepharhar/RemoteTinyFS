// controls the block meta panel of the main page

polyFS_visualizer.block_meta = {

   update_block_meta: function(block){
      console.log("updating block metadata");

      var meta_text = "";
      if(block != null){
         var current_block = polyFS_visualizer.data.get_block(block);
         var meta_text = "block number " + block + "<br />";
      }
      else {
         meta_text = "select a block<br />";
      }

      for(index in polyFS_visualizer.config.block_config.bytes){
         meta_text += "<b>" + polyFS_visualizer.config.block_config.bytes[index] + "</b>: " 
         if(block != null){
            meta_text += "0x" + current_block.get_byte(index).toString(16);
            meta_text += ", " + current_block.get_byte(index);
            meta_text += (polyFS_visualizer.block_meta.metadata_config[polyFS_visualizer.config.block_config.bytes[index]] || 
                          polyFS_visualizer.block_meta.metadata_config["default"])(current_block);
         }
         meta_text += "<br />";
      }
      $("#blockmeta").html(meta_text);
   },

   metadata_config: {
      "type": function (block){
            return ", " + polyFS_visualizer.config.get_data_config("type", block.get_byte(polyFS_visualizer.config.block_config.bytes.indexOf("type")));
         },
      "magic-number": function (block){
            return ", " + polyFS_visualizer.config.get_data_config("magic-number", block.get_byte(polyFS_visualizer.config.block_config.bytes.indexOf("magic-number")));
         },
      "link": function (block){
            var linked_block = polyFS_visualizer.data.blocks[block.get_byte(polyFS_visualizer.config.block_config.bytes.indexOf("link"))];
            if(!linked_block){
               return "";
            }
            return ", linking to a " + polyFS_visualizer.config.get_data_config("type", linked_block.get_byte(polyFS_visualizer.config.block_config.bytes.indexOf("type")));
         },
      "default": function (block){
            return "";
         },
   }
}
