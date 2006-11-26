CREATE TABLE `config_vacmodes` (
  `id` int(11) NOT NULL auto_increment,
  `dauer` int(10) unsigned NOT NULL default '1',
  `vorlauf` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `config_vacmodes` (`id`, `dauer`, `vorlauf`) VALUES (1, 35, 14),
(2, 49, 14),
(3, 98, 14),
(4, 147, 14),
(5, 196, 14);