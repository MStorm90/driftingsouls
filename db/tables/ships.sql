CREATE TABLE `ships` (
  `id` int(11) NOT NULL auto_increment,
  `owner` int(11) NOT NULL default '0',
  `name` varchar(50) NOT NULL default 'noname',
  `type` int(11) NOT NULL default '0',
  `cargo` text NOT NULL,
  `x` int(11) NOT NULL default '1',
  `y` int(11) NOT NULL default '1',
  `system` tinyint(4) NOT NULL default '1',
  `status` varchar(255) NOT NULL default '',
  `crew` int(11) unsigned NOT NULL default '0',
  `e` int(11) unsigned NOT NULL default '0',
  `s` int(11) unsigned NOT NULL default '0',
  `hull` int(11) unsigned NOT NULL default '1',
  `shields` int(11) unsigned NOT NULL default '0',
  `heat` text NOT NULL,
  `engine` int(11) NOT NULL default '100',
  `weapons` int(11) NOT NULL default '100',
  `comm` int(11) NOT NULL default '100',
  `sensors` int(11) NOT NULL default '100',
  `docked` varchar(20) NOT NULL default '',
  `alarm` int(11) NOT NULL default '0',
  `fleet` int(11) NOT NULL default '0',
  `destsystem` int(11) NOT NULL default '0',
  `destx` int(11) NOT NULL default '0',
  `desty` int(11) NOT NULL default '0',
  `destcom` text NOT NULL,
  `bookmark` tinyint(1) unsigned NOT NULL default '0',
  `battle` int(11) NOT NULL default '0',
  `battleAction` tinyint(1) unsigned NOT NULL default '0',
  `jumptarget` varchar(100) NOT NULL default '',
  `autodeut` tinyint(3) unsigned NOT NULL default '1',
  `history` text NOT NULL,
  `script` text,
  `scriptexedata` BLOB,
  `oncommunicate` text,
  `lock` varchar(9) default NULL,
  `visibility` mediumint(9) default NULL,
  `onmove` text,
  `respawn` tinyint(4) default NULL,
  PRIMARY KEY  (`id`),
  KEY `coords` (`x`,`y`,`system`),
  KEY `owner` (`owner`),
  KEY `battle` (`battle`),
  KEY `status` (`status`),
  KEY `bookmark` (`bookmark`),
  KEY `type` (`type`),
  KEY `docked` (`docked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PACK_KEYS=0; 

ALTER TABLE ships ADD CONSTRAINT ships_fk_users FOREIGN KEY (owner) REFERENCES users(id);
ALTER TABLE ships ADD CONSTRAINT ships_type_fk FOREIGN KEY (type) REFERENCES ship_types(id);

INSERT INTO `ships` (`id`, `owner`, `name`, `type`, `cargo`, `x`, `y`, `system`, `status`, `crew`, `e`, `s`, `hull`, `shields`, `heat`, `engine`, `weapons`, `comm`, `sensors`, `docked`, `alarm`, `fleet`, `destsystem`, `destx`, `desty`, `destcom`, `bookmark`, `battle`, `battleAction`, `jumptarget`, `autodeut`, `history`, `script`, `scriptexedata`, `oncommunicate`, `lock`, `visibility`, `onmove`, `respawn`) VALUES (2, -1, 'Frachter', 27, '0,0,0,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,', 1, 1, 0, 'noconsign', 50, 80, 0, 5000, 0, '', 100, 100, 100, 100, '', 0, 0, 0, 0, 0, '', 0, 0, 0, '', 1, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `ships` (`id`, `owner`, `name`, `type`, `cargo`, `x`, `y`, `system`, `status`, `crew`, `e`, `s`, `hull`, `shields`, `heat`, `engine`, `weapons`, `comm`, `sensors`, `docked`, `alarm`, `fleet`, `destsystem`, `destx`, `desty`, `destcom`, `bookmark`, `battle`, `battleAction`, `jumptarget`, `autodeut`, `history`, `script`, `scriptexedata`, `oncommunicate`, `lock`, `visibility`, `onmove`, `respawn`) VALUES (3, -1, 'Tanker', 2, '0,300,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,', 1, 2, 0, 'noconsign', 30, 40, 0, 10000, 0, '', 100, 100, 100, 100, '', 0, 0, 0, 0, 0, '', 0, 0, 0, '', 1, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `ships` (`id`, `owner`, `name`, `type`, `cargo`, `x`, `y`, `system`, `status`, `crew`, `e`, `s`, `hull`, `shields`, `heat`, `engine`, `weapons`, `comm`, `sensors`, `docked`, `alarm`, `fleet`, `destsystem`, `destx`, `desty`, `destcom`, `bookmark`, `battle`, `battleAction`, `jumptarget`, `autodeut`, `history`, `script`, `scriptexedata`, `oncommunicate`, `lock`, `visibility`, `onmove`, `respawn`) VALUES (5, -1, 'Frachter', 31, '0,60,0,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,', 2, 1, 0, 'noconsign', 30, 80, 0, 8500, 0, '', 100, 100, 100, 100, '', 0, 0, 0, 0, 0, '', 0, 0, 0, '', 1, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `ships` (`id`, `owner`, `name`, `type`, `cargo`, `x`, `y`, `system`, `status`, `crew`, `e`, `s`, `hull`, `shields`, `heat`, `engine`, `weapons`, `comm`, `sensors`, `docked`, `alarm`, `fleet`, `destsystem`, `destx`, `desty`, `destcom`, `bookmark`, `battle`, `battleAction`, `jumptarget`, `autodeut`, `history`, `script`, `scriptexedata`, `oncommunicate`, `lock`, `visibility`, `onmove`, `respawn`) VALUES (6, -1, 'Tanker', 30, '0,240,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,', 2, 2, 0, 'noconsign', 30, 50, 0, 4000, 0, '', 100, 100, 100, 100, '', 0, 0, 0, 0, 0, '', 0, 0, 0, '', 1, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `ships` (`id`, `owner`, `name`, `type`, `cargo`, `x`, `y`, `system`, `status`, `crew`, `e`, `s`, `hull`, `shields`, `heat`, `engine`, `weapons`, `comm`, `sensors`, `docked`, `alarm`, `fleet`, `destsystem`, `destx`, `desty`, `destcom`, `bookmark`, `battle`, `battleAction`, `jumptarget`, `autodeut`, `history`, `script`, `scriptexedata`, `oncommunicate`, `lock`, `visibility`, `onmove`, `respawn`) VALUES (666, -2, 'GTU Handelsposten Delta Serpentis', 10, '0,2000,0,0,500,2000,0,0,0,0,0,0,5000,0,0,0,0,0,300|10|0|0;301|3|0|0;325|3|0|0;326|3|0|0;161|500|0|0;170|500|0|0;150|500|0|0;154|500|0|0;172|500|0|0', 75, 75, 1, 'tradepost nocrew', 0, 4700, 0, 100000, 0, '', 100, 100, 100, 100, '', 0, 0, 0, 0, 0, '', 0, 0, 0, '2:150/150', 1, '', NULL, NULL, '*:26:0', NULL, NULL, NULL, NULL);