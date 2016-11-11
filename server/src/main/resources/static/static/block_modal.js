
polyFS_visualizer.block_modal = {

   show_block_callback: function(block, byte_num){
      return function(layer){
         var number = layer.name.substring(1);
         var byte_num = layer.name.substring(0, 1) == "h" ? Math.floor(number/2) : number;
         var link = parseInt(block.get_byte(byte_num));
         $('#blockModal').modal('hide');
         if(link < 0 || link >= polyFS_visualizer.data.blocks.length){
            warning("invalid link");
            return;
         }
         $("#block-number").val(link);
         setTimeout(function(){ $('#blockModal').modal('show'); }, 600);
      };
   },

   bold_link_callback: function(block, byte_num){
      // type is either "c" for char or "h" for hex

      if(polyFS_visualizer.config.block_config.bytes[parseInt(byte_num)] != "link"){
         return null;
      }
      return function(layer) {
         $("#blockCanvas").setLayerGroup("byte" + byte_num, {"fontStyle": "bold"}).drawLayers();
      }
   },

   unbold_link_callback: function(block, byte_num){
      if(polyFS_visualizer.config.block_config.bytes[parseInt(byte_num)] != "link"){
         return null;
      }
      return function(layer) {
         $("#blockCanvas").setLayerGroup("byte" + byte_num, {"fontStyle": "normal"}).drawLayers();
      }
   },


   draw_hex: function(block, char_width, char_height, char_per_line){
      
         
      for(i = 0; i < block.hex_data.length; i++){
      
         // draw character
         var color = polyFS_visualizer.config.get_display_config("byte", "fillstyle", polyFS_visualizer.config.block_config.bytes[Math.floor(i/2)]);
         var x = (i % char_per_line) * char_width + Math.floor((i % char_per_line)/4) * char_width;
         var y = Math.floor(i/char_per_line) * char_height + char_height;
         polyFS_visualizer.draw_utils.draw_text("blockCanvas", x, y, block.hex_data[i], color, "h" + i);
         
         if(polyFS_visualizer.config.block_config.bytes[Math.floor(i/2)] == "link"){
            console.log("found link");
            $('#blockCanvas').setLayer("h" + i, {
               "groups": ["byte" + Math.floor(i/2)],
               "click": polyFS_visualizer.block_modal.show_block_callback(block), 
               "mouseover": polyFS_visualizer.block_modal.bold_link_callback(block, Math.floor(i/2).toString()),
               "mouseout": polyFS_visualizer.block_modal.unbold_link_callback(block, Math.floor(i/2).toString())
            }).drawLayers();
         }
      }
   },

   draw_char: function(block){
      
      // width of a character
      char_width = 15;
      // height of a character
      char_height = 19;
      // characters per line
      char_per_line = 12;
      
      for(i = 0; i < block.char_data.length; i++){

         // draw character
         var color = polyFS_visualizer.config.get_display_config("byte", "fillstyle", polyFS_visualizer.config.block_config.bytes[i]);
         var x = (i % char_per_line) * char_width + 350;
         var y = Math.floor(i / char_per_line) * char_height + char_height;
         polyFS_visualizer.draw_utils.draw_text("blockCanvas", x, y, block.char_data[i], color, "c" + i);
         
         if(polyFS_visualizer.config.block_config.bytes[i] == "link"){
            console.log("found link");
            $('#blockCanvas').setLayer("c" + i, {
               "groups": ["byte" + i],
               "click": polyFS_visualizer.block_modal.show_block_callback(block), 
               "mouseover": polyFS_visualizer.block_modal.bold_link_callback(block, Math.floor(i/2), "c"), 
               "mouseout": polyFS_visualizer.block_modal.unbold_link_callback(block, Math.floor(i/2), "c")
            }).drawLayers();
         }
      }
   },

   // draws the block modal canvas
   update_block: function(block){
      console.log("updating block canvas");
   
      var char_width = 10;
      var char_height = 19;
      var char_per_line = 24;

      var legend_x = 550;
      var legend_y;
      
      // set height of canvas
      $("#blockCanvasDiv").html('<canvas id="blockCanvas"></canvas>');
      var canvas = document.getElementById('blockCanvas');
      var height = Math.ceil(block.hex_data.length / char_per_line) * char_height + char_height*2;
      
      canvas.height = Math.max(height, 200);
      canvas.width = 800;

      $('canvas').detectPixelRatio(function(ratio) {});

      $('#blockCanvas').removeLayers();
      $('#blockCanvas').clearCanvas();
         
      // draw titles 
      polyFS_visualizer.draw_utils.draw_text("blockCanvas", 0, 0, "HEX", "#000000", "t1");
      polyFS_visualizer.draw_utils.draw_text("blockCanvas", 350, 0, "CHAR", "#000000", "t2");

      // HEX
      polyFS_visualizer.block_modal.draw_hex(block, char_width, char_height, char_per_line);

      // SEPERATOR
      $('#blockCanvas').drawLine({
         layer: true,
         strokeStyle: '#000',
         strokeWidth: 1,
         x1: 320.5, y1: 0.5,
         x2: 320.5, y2: canvas.height + 0.5,
      });
      
      // CHAR
      polyFS_visualizer.block_modal.draw_char(block);
      
      // LEGEND
      //legend_y = Math.max(parseInt(canvas.style.height.substring(0, canvas.style.height.length - 2)) / 2 - 100, 0);
      //legend_y = Math.max(canvas.height /2 - 100, 0);
      legend_y = 0;
      legend_items = polyFS_visualizer.config.display_config.byte.fillstyle.mapping;
      legend_items["unrecognised"] = polyFS_visualizer.config.display_config.byte.fillstyle.default;
      polyFS_visualizer.draw_utils.draw_legend("blockCanvas", legend_x, legend_y, legend_items, "byte");
   },

}
